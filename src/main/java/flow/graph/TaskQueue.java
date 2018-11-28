package flow.graph;

public interface TaskQueue {

    Task<?> getTask() throws InterruptedException;

    void addTask(Task<?> task) throws InterruptedException;

    boolean isEmpty();
}
