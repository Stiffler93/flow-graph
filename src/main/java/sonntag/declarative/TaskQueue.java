package sonntag.declarative;

public interface TaskQueue {

    Task<?, ?> getTask() throws InterruptedException;

    void addTask(Task<?, ?> task);

    TaskQueue onFinished(Node<?, ?> node);

    void triggerExecution();
}
