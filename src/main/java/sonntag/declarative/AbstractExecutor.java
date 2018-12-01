package sonntag.declarative;

import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractExecutor implements Executor {

    private static Logger logger = Logger.getLogger(AbstractExecutor.class.getName());

    protected static int numberOfExecutors = 0;

    protected TaskQueue taskQueue;
    protected int executorNumber;

    protected boolean shallExecute = true;

    public AbstractExecutor(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;

        executorNumber = ++numberOfExecutors;
        logger.info(String.format("Created: %s(%d).", getClass().getSimpleName(), executorNumber));
    }

    @Override
    public void run() {
        logger.info(String.format("Started: %s(%d).", getClass().getSimpleName(), executorNumber));

        while(shallExecute) {

            try {
                Task<?> task = taskQueue.getTask();
                List<Task<?>> tasks = task.execute();

                for(Task<?> t : tasks) {
                    taskQueue.addTask(t);
                }
            } catch (InterruptedException e) {
                logger.severe(e.getStackTrace().toString());
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    logger.severe(e2.getStackTrace().toString());
                }
            }
        }

        logger.info(String.format("Shutdown: %s(%d).", getClass().getSimpleName(), executorNumber));
    }

    public void prepareShutdown() {
        shallExecute = false;
    }
}
