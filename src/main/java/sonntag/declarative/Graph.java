package sonntag.declarative;

import sonntag.declarative.states.State;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class Graph<T, V> extends AbstractNode<T, V> implements Runnable {

    private static Logger logger = Logger.getLogger(Graph.class.getName());

    private int numExecutors = 1;
    private Node<T, ?> startNode;
    private Node<?, V> endNode;
    private T input;
    private Class<? extends TaskQueue> taskQueueClass = TaskQueueImpl.class;
    private TaskQueue taskQueue;
    private Class<? extends Executor> executorClass = ExecutorImpl.class;

    private Graph(Node<T, ?> startNode) {
        super("Graph_" + UUID.randomUUID().toString());
        this.startNode = startNode;
    }

    public Graph<T, V> input(T input) {
        this.input = input;
        return this;
    }

    public Graph<T, V> parallel(int numExecutors) {
        this.numExecutors = numExecutors;
        return this;
    }

    public Graph<T, V> asQueue(Class<? extends TaskQueue> taskQueueClass) {
        this.taskQueueClass = taskQueueClass;
        return this;
    }

    public Graph<T, V> asExecutor(Class<? extends Executor> executorClass) {
        this.executorClass = executorClass;
        return this;
    }

    public Graph<T, V> atLast(Node<?, V> endNode) {
        this.endNode = endNode;
        return this;
    }

    public static <T, V> Graph<T, V> start(Node<T, ?> startNode) {
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

        taskQueue.addTask(Task.of(input, startNode::trigger));

        if (endNode != null) {
            taskQueue.onFinished(endNode);
        }

        taskQueue.triggerExecution();
    }

    @Override
    protected V execute(T data, State state) {
        this.input = data;
        run();

        return null;
    }

}
