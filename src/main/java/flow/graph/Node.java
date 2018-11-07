package flow.graph;

import flow.Action;
import flow.states.CleanState;
import flow.states.State;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Node<T, V> {

    private final String ID;
    private List<NodeConnection<V>> nodeConnections;
    private State state;

    public Node(String id) {
        this(id, new CleanState());
    }

    public Node(String id, State state) {
        ID = id;
        nodeConnections = new ArrayList<>(5);
        this.state = state;
    }

    protected abstract V execute(T data, State state, Action action);

    public final void then(Node<V, ?>... node) {
        for(Node<V, ?> n : node) {
            nodeConnections.add(new NodeConnection<>(n));
        }
    }

    public final void then(Node<V, ?> node, Predicate<V> predicate) {
        nodeConnections.add(new NodeConnection<>(node, predicate));
    }

    public final State getState() {
        return state;
    }

    public final String getIdentifier() {
        return ID;
    }

    public final void remove(String id) {
        nodeConnections.removeIf(nc -> nc.node.getIdentifier().equals(id));
    }

    public State determineState() {
        return null;
    }

    protected final void trigger(T data, Action action) {
        if(stop())
            return;

        State newState = determineState();
        if(newState != null)
            state = newState;

        V result = execute(data, state, action);
        forward(result, action);
    }

    public boolean stop() {
        return false;
    }

    public void reset() {
    }

    protected final void clean() {
        if(state.getIdentifier().equals(CleanState.ID))
            return;

        reset();
        nodeConnections.stream().forEach(c -> {
            c.node.clean();
        });
    }

    protected final void forward(V data, Action action) {
        nodeConnections.stream().forEach(c -> {
            if(c.predicate.test(data))
                c.node.trigger(data, action);
        });
    }

    protected void setState(State state) {
        this.state = state;
    }

    private static class NodeConnection<A> {
        Node<A, ?> node;
        Predicate<A> predicate;

        public NodeConnection(Node<A, ?> node) {
            this(node, (Predicate<A>) t -> { return true; });
        }

        public NodeConnection(Node<A, ?> node, Predicate<A> predicate) {
            this.node = node;
            this.predicate = predicate;
        }
    }
}
