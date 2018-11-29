package flow.graph;

import java.util.ArrayList;
import java.util.List;

public class EmptyTask extends Task<String> {

    public EmptyTask() {
        super("null", null, null);
    }

    List<Task<?>> execute() {
        return new ArrayList<>();
    }
}
