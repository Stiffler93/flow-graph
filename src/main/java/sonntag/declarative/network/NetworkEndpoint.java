package sonntag.declarative.network;

import sonntag.declarative.states.ActiveState;
import sonntag.declarative.states.PassiveState;
import sonntag.declarative.states.State;

public abstract class NetworkEndpoint<T, V> extends Endpoint<T, V> {

    public NetworkEndpoint(String id, State state) {
        super(id, state);
    }

    @Override
    protected V execute(T data, State state) {
        if(state.equals(PassiveState.ID)) {
            return receive(data, state);
        } else if(state.equals(ActiveState.ID)) {
            return send(data, state);
        }

        throw new IllegalAccessError("Endpoint was executed with wrong State!");
    }

    protected abstract V send(T data, State state);
    protected abstract V receive(T data, State state);
}
