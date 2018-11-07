package flow.states;

public abstract class AbstractState implements State{

    protected String id;

    public AbstractState(String id) {
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        return id;
    }
}
