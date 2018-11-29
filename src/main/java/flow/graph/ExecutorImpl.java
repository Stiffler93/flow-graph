package flow.graph;

import java.util.List;
import java.util.logging.Logger;

public class ExecutorImpl implements Executor {

    private static Logger logger = Logger.getLogger(ExecutorImpl.class.getName());

    private static int numberOfExecutors = 0;

    private TaskQueue queue;
    private int executorNumber;
    private boolean shallExecute = true;

    public ExecutorImpl(TaskQueue queue) {
        this.queue = queue;

        executorNumber = ++numberOfExecutors;
        logger.info(String.format("Created: %s(%d).", getClass().getSimpleName(), executorNumber));
    }

    @Override
    public void run() {
        logger.info(String.format("Started: %s(%d).", getClass().getSimpleName(), executorNumber));

        while(shallExecute) {

            try {
                Task<?> task = queue.getTask();
                List<Task<?>> tasks = task.execute();

                for(Task<?> t : tasks) {
                    queue.addTask(t);
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

    @Override
    public void stopExecution() {
        shallExecute = false;
    }
}
