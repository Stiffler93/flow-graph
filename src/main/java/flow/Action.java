package flow;

import flow.states.State;

public interface Action {

    public State execute();
}
