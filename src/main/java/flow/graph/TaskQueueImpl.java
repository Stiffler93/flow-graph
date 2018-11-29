package flow.graph;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class TaskQueueImpl implements TaskQueue {

    private static Logger logger = Logger.getLogger(TaskQueueImpl.class.getName());

    protected Queue<Task<?>> queue;
    private List<Executor> executors;

    private boolean isExecuting = false;
    private final int numExecutors;
    private int numThreadsWaiting = 0;

    private ThreadGroup threadGroup;
    private ReentrantLock lock = new ReentrantLock();
    private Condition queueNotEmpty = lock.newCondition();

    public TaskQueueImpl() {
        this(ExecutorImpl.class, 1);
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
            throw new IllegalStateException("Initialization of Executor failed!");
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
            task = queue.poll();
        } finally {
            lock.unlock();
        }

        if (task == null)
            throw new IllegalAccessError(
                    String.format("%s: Could not retrieve Task from empty queue!", Thread.currentThread().getName()));

        return task;
    }

    private boolean stopConditionMet() {
        return numThreadsWaiting + 1 == numExecutors;
    }

    private void shutdown() {
        for (Executor executor : executors) {
            executor.stopExecution();
        }

        for (int i = 0; i < numExecutors; i++) {
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
            logger.fine(String.format("%s: %d tasks in queue.", Thread.currentThread().getName(), queue.size()));
            lock.unlock();
        }
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
