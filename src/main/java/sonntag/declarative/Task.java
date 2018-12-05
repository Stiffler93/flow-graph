package sonntag.declarative;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class Task<T> {

    private static Logger logger = Logger.getLogger(Task.class.getName());

    private final UUID uuid;
    private final T inputData;
    private final Trigger<T> trigger;
    private TaskResult result;

    Task(T inputData, Trigger<T> trigger) {
        uuid = UUID.randomUUID();
        logger.finer(String.format("Created: %s(\"%s\", input: %s).", getClass().getSimpleName(), uuid.toString(), inputData.getClass().getSimpleName()));

        this.inputData = inputData;
        this.trigger = trigger;
    }

    // create a wrapper class and return not only the Task list, but also the output/result of the executed task
    List<Task<?>> execute() {
        logger.finer(String.format("Execute: %s(\"%s\", input: %s).", getClass().getSimpleName(), uuid.toString(), inputData.getClass().getSimpleName()));
        return trigger.trigger(inputData);
    }

    public TaskResult getResult() {
        return result;
    }

    static <T>Task of(T inputData, Trigger<T> trigger) {
        return new Task(inputData, trigger);
    }

    static <T>Task of(Task<T> task, Trigger trigger) { return new Task(task.inputData, trigger); }


    static final class TaskResult<V> {

        private final V result;
        private final List<Task<?>> tasks;

        public TaskResult(V result) {
            this(result, new ArrayList<>());
        }

        public TaskResult(V result, List<Task<?>> tasks) {
            this.result = result;
            this.tasks = tasks;
        }
    }
}
