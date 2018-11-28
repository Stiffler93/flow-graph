package flow.graph;

import flow.Action;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Task<T> {

    private static Logger logger = Logger.getLogger(Task.class.getName());

    private UUID uuid;
    private T inputData;
    private Action action;
    private Trigger<T> trigger;

    private Task(T inputData, Action action, Trigger<T> trigger) {
        uuid = UUID.randomUUID();
        logger.finer(String.format("Created: %s(\"%s\", input: %s).", getClass().getSimpleName(), uuid.toString(), inputData.getClass().getSimpleName()));

        this.inputData = inputData;
        this.action = action;
        this.trigger = trigger;
    }

    List<Task<?>> execute() {
        logger.finer(String.format("Execute: %s(\"%s\", input: %s).", getClass().getSimpleName(), uuid.toString(), inputData.getClass().getSimpleName()));
        return trigger.trigger(inputData, action);
    }

    static <T>Task of(T inputData, Action action, Trigger<T> trigger) {
        return new Task(inputData, action, trigger);
    }
}
