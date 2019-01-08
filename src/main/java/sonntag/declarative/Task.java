package sonntag.declarative;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public /*final*/ class Task<T, V> {

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
    List<Task<?, ?>> execute() {
        logger.finer(String.format("Execute: %s(\"%s\", input: %s).", getClass().getSimpleName(), uuid.toString(), inputData.getClass().getSimpleName()));
//        throw new UnsupportedOperationException("Take care of this generic!");
        return trigger.trigger(inputData);
    }

    public TaskResult getResult() {
        return result;
    }

    static <T, V> Task<T, V> of(T inputData, Trigger<T> trigger) {
        return new Task<T, V>(inputData, trigger);
    }

    static <T, V> Task<T, V> of(Task<T, V> task, Trigger<T> trigger) {
        return new Task<>(task.inputData, trigger);
    }


    static final class TaskResult<V> {

        private final V result;
        private final List<Task<V, ?>> tasks;

        public TaskResult(V result) {
            this(result, new ArrayList<>());
        }

        public TaskResult(V result, List<Task<V, ?>> tasks) {
            this.result = result;
            this.tasks = tasks;
        }
    }
}
