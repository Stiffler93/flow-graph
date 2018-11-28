package example;

import flow.Action;
import flow.graph.Node;
import flow.states.DirtyState;
import flow.states.State;

import java.util.List;

public class ConcatStringsNode extends Node<List<String>, String> {

    private int counter = 0;

    public ConcatStringsNode(String id) {
        super(id);
    }

    public ConcatStringsNode(String id, State state) {
        super(id, state);
    }

    @Override
    public String execute(List<String> data, State state, Action action) {
        setState(new DirtyState());
        counter++;

        StringBuilder builder = new StringBuilder();
        for(String s : data)
            builder.append(s);

        return builder.toString();
    }

    @Override
    public boolean stop() {
        return counter >= 100;
    }

    @Override
    public void reset() {
        counter = 0;
    }
}
