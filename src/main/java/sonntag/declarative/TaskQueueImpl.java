package sonntag.declarative;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

class TaskQueueImpl implements TaskQueue {

    private static Logger logger = Logger.getLogger(TaskQueueImpl.class.getName());

    private Queue<Task<?>> queue;
    private List<Executor> executors;
    private Node<?, ?> finalNode;
    private Task<?> lastProcessedTask;

    private boolean isExecuting = false;
    private final int numExecutors;
    private int numThreadsWaiting = 0;

    private ThreadGroup threadGroup;
    private ReentrantLock lock = new ReentrantLock();
    private Condition queueNotEmpty = lock.newCondition();

    public TaskQueueImpl() {
        this(1);
    }

    public TaskQueueImpl(int numExecutors) {
        this(ExecutorImpl.class, numExecutors);
    }

    public TaskQueueImpl(Class<? extends Executor> executorClass, int numExecutors) {
        queue = new LinkedList<>();
        executors = new ArrayList<>(numExecutors);
        this.numExecutors = numExecutors;

        try {
            Constructor constructor = executorClass.getConstructor(TaskQueue.class);
            for (int i = 0; i < numExecutors; i++) {
                executors.add((Executor) constructor.newInstance(this));
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe(e.getStackTrace().toString());
            throw new IllegalStateException("Initialization of Executors failed!");
        }

        logger.info(String.format("Created: %s.", getClass().getSimpleName()));
    }

    @Override
    public Task<?> getTask() throws InterruptedException {
        lock.lock();

        Task<?> task;

        try {

            while (queue.isEmpty()) {

                if (stopConditionMet()) {
                    shutdown();
                } else {
                    logger.fine(String.format("%s: Wait.", Thread.currentThread().getName()));
                    numThreadsWaiting++;
                    queueNotEmpty.await();
                    numThreadsWaiting--;
                }
            }

            logger.fine(String.format("%s: Retrieve element.", Thread.currentThread().getName()));
            lastProcessedTask = task = queue.poll();
        } finally {
            lock.unlock();
        }

        if (task == null)
            throw new IllegalAccessError(
                    String.format("%s: Could not retrieve Task from empty taskQueue!", Thread.currentThread().getName()));

        return task;
    }

    private boolean stopConditionMet() {
        return queue.isEmpty() && numThreadsWaiting + 1 == numExecutors;
    }

    private void shutdown() {
        for (Executor executor : executors) {
            executor.prepareShutdown();
        }

        int taskCreated = 0;
        if(finalNode != null && lastProcessedTask != null) {
            queue.offer(Task.of(lastProcessedTask, finalNode));
            taskCreated = 1;
        }

        for (int i = 0; i < numExecutors - taskCreated; i++) {
            queue.offer(new EmptyTask());
        }

        queueNotEmpty.signalAll();
    }

    @Override
    public void addTask(Task<?> task) {
        lock.lock();

        try {
            boolean wasQueueEmpty = queue.isEmpty();
            queue.offer(task);

            if (wasQueueEmpty)
                queueNotEmpty.signalAll();
        } finally {
            logger.fine(String.format("%s: %d tasks in taskQueue.", Thread.currentThread().getName(), queue.size()));
            lock.unlock();
        }
    }

    @Override
    public TaskQueue onFinished(Node<?, ?> node) {
        this.finalNode = node;
        return this;
    }

    @Override
    public void triggerExecution() {
        if (!isExecuting) {
            isExecuting = true;

            threadGroup = new ThreadGroup("Executors");
            for (int i = 0; i < executors.size(); i++) {
                new Thread(threadGroup, executors.get(i), String.format("Executor-%d", i)).start();
            }
        }
    }

}
