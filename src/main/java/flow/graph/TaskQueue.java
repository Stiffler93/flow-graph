package flow.graph;

public interface TaskQueue {

    Task<?> getTask() throws InterruptedException;

    void addTask(Task<?> task);

    void triggerExecution();
}
