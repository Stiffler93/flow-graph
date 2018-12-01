package sonntag.declarative;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

public final class Graph<T> implements Runnable {

    private static Logger logger = Logger.getLogger(Graph.class.getName());

    private int numExecutors = 1;
    private Node<T, ?> startNode;
    private T input;
    private Class<? extends TaskQueue> taskQueueClass = TaskQueueImpl.class;
    private TaskQueue taskQueue;
    private Class<? extends Executor> executorClass = ExecutorImpl.class;

    private Graph(Node<T, ?> startNode) {
        this.startNode = startNode;
    }

    public Graph<T> input(T input) {
        this.input = input;
        return this;
    }

    public Graph<T> parallel(int numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public Graph<T> asQueue(Class<? extends TaskQueue> taskQueueClass) {
        this.taskQueueClass = taskQueueClass;
        return this;
    }

    public Graph<T> asExecutor(Class<? extends Executor> executorClass) {
        this.executorClass = executorClass;
        return this;
    }

    public static <T> Graph<T> start(Node<T, ?> startNode) {
        return new Graph<>(startNode);
    }

    @Override
    public void run() {
        logger.info(String.format("Execute: %s(input: %s, numExecutors: %d, taskQueue: %s, executor: %s)", getClass().getSimpleName(),
                input.getClass().getSimpleName(), numExecutors, taskQueueClass.getSimpleName(), executorClass.getSimpleName()));

        if (startNode == null) {
            throw new IllegalStateException("Tried to run graph without a Start Node!");
        }

        try {
            Constructor constructor = taskQueueClass.getConstructor(Class.class, int.class);
            taskQueue = (TaskQueue) constructor.newInstance(executorClass, numExecutors);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe(e.getStackTrace().toString());
            throw new IllegalStateException("Initialization of TaskQueue failed!");
        }

        List<Task<?>> tasks = startNode.trigger(input);
        for (Task<?> task : tasks) {
            taskQueue.addTask(task);
        }

        taskQueue.triggerExecution();
    }

    public void clean() {
        startNode.clean();
    }

}
