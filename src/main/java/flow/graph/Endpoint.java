package flow.graph;

import flow.Action;
import flow.states.State;

public abstract class Endpoint<T, V> extends Node<T, V>{

    public Endpoint(String id) {
        super(id);
        initialize();
    }

    public Endpoint(String id, State state) {
        super(id, state);
        initialize();
    }

    public abstract void initialize();
}
