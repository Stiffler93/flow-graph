package sonntag.declarative.network;

import sonntag.declarative.AbstractNode;
import sonntag.declarative.states.State;

public abstract class Endpoint<T, V> extends AbstractNode<T, V> {

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
