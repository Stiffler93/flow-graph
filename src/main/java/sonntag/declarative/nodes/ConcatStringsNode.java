package sonntag.declarative.nodes;

import sonntag.declarative.AbstractNode;
import sonntag.declarative.states.DirtyState;
import sonntag.declarative.states.State;

import java.util.List;

public class ConcatStringsNode extends AbstractNode<List<String>, String> {

    public ConcatStringsNode(String id) {
        super(id);
    }

    public ConcatStringsNode(String id, State state) {
        super(id, state);
    }

    @Override
    public String execute(List<String> data, State state) {
        StringBuilder builder = new StringBuilder();
        for(String s : data)
            builder.append(s);

        return builder.toString();
    }
}
