package flow.graph;

import flow.Action;
import flow.states.ActiveState;
import flow.states.PassiveState;
import flow.states.State;

public abstract class NetworkEndpoint<T, V> extends Endpoint<T, V> {

    public NetworkEndpoint(String id, State state) {
        super(id, state);
    }

    @Override
    protected V execute(T data, State state, Action action) {
        if(state.equals(PassiveState.ID)) {
            return receive(data, state, action);
        } else if(state.equals(ActiveState.ID)) {
            return send(data, state, action);
        }

        throw new IllegalAccessError("Endpoint was executed with wrong State!");
    }

    protected abstract V send(T data, State state, Action action);
    protected abstract V receive(T data, State state, Action action);
}
