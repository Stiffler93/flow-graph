package sonntag.declarative;

import java.util.ArrayList;
import java.util.List;

class EmptyTask extends Task<String, Void> {

    public EmptyTask() {
        super("null", null);
    }

    List<Task<?, ?>> execute() {
        return new ArrayList<>();
    }
}
