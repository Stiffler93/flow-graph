package flow.graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class TaskQueueImpl implements TaskQueue {

    private static Logger logger = Logger.getLogger(TaskQueueImpl.class.getName());
    protected BlockingQueue<Task<?>> queue;

    private Object getLock = new Object();
    private Object addLock = new Object();

    public TaskQueueImpl() {
        queue = new ArrayBlockingQueue<>(200, true);
        logger.finer(String.format("Created: %s.", getClass().getSimpleName()));
    }

    public Task<?> getTask() throws InterruptedException {
        logger.info(String.format("%s-getTask: %d tasks in queue.", getClass().getSimpleName(), queue.size()));
        synchronized (getLock) {
            // check here somehow the lock, how many threads are waiting, If all other threads are waiting and
            // queue is empty -> return "Destroy Task" (implement this functionality into the Lock!)
            // Additionally, in case lock is acquired, queue empty and running processes do not return new Tasks
            // and wait to acquire lock -> Deadlock (fix it with offer(ms) method to wait only for a period of time
            // for the queue and if nothing returns return an "empty run"-Task that does not do anything (just to
            // not run into a Deadlock)
            return queue.take();
        }
    }

    public void addTask(Task<?> task) throws InterruptedException {
//        synchronized (addLock) {
            queue.put(task);
//        }
        logger.info(String.format("%s-addTask: %d tasks in queue.", getClass().getSimpleName(), queue.size()));
    }

    @Override
    public boolean isEmpty() {
        synchronized (getLock) {
            return queue.isEmpty();
        }
    }
}
