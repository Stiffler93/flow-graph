package flow.graph;

import java.util.List;
import java.util.logging.Logger;

public class ExecutorImpl implements Executor {

    private static Logger logger = Logger.getLogger(ExecutorImpl.class.getName());

    private static int numberOfExecutors = 0;

    private TaskQueue queue;
    private int executorNumber;

    public ExecutorImpl(TaskQueue queue) {
        this.queue = queue;

        executorNumber = ++numberOfExecutors;
        logger.finer(String.format("Created: %s(%d).", getClass().getSimpleName(), executorNumber));
    }

    @Override
    public void run() {
        logger.info(String.format("Started: %s(%d).", getClass().getSimpleName(), executorNumber));

        while(true) {

            try {
                Task<?> task = queue.getTask();
                List<Task<?>> tasks = task.execute();

                // if no tasks returned and task queue empty ??? and no threads working ???
                if(tasks.isEmpty() && queue.isEmpty()) {
                    return;
                }

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
    }
}
