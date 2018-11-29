package flow.graph;

import flow.Action;
import flow.states.CleanState;
import flow.states.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class Node<T, V> implements Trigger<T> {

    private static Logger logger = Logger.getLogger(Node.class.getName());

    private UUID uuid = UUID.randomUUID();
    private final String ID;
    private List<NodeConnection<V>> nodeConnections;
    private State state;

    public Node(String id) {
        this(id, new CleanState());
    }

    public Node(String id, State state) {
        logger.finer(String.format("Created: %s(\"%s\", id: %s, State: %s", getClass().getSimpleName(), uuid.toString(), id, state.getIdentifier()));

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

    @Override
    public final List<Task<?>> trigger(T data, Action action) {
        logger.info(String.format("%s-trigger: id=%s, input=%s", getClass().getSimpleName(), getIdentifier(), data.getClass().getSimpleName()));
        if(stop())
            return new ArrayList<>();

        State newState = determineState();
        if(newState != null)
            state = newState;

        V result = execute(data, state, action);

        return forward(result, action);
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

    protected final List<Task<?>> forward(V data, Action action) {
        List<Task<?>> tasks = new ArrayList<>();
        nodeConnections.stream().forEach(c -> {
            if(c.predicate.test(data))
                tasks.add(Task.of(data, action, c.node::trigger));
        });

        logger.info(String.format("%s-forward: %d new tasks created.", getClass().getSimpleName(), tasks.size()));

        return tasks;
    }

    protected void setState(State state) {
        this.state = state;
    }

    private class NodeConnection<A> {

        private Logger logger = Logger.getLogger(NodeConnection.class.getName());
        Node<A, ?> node;
        Predicate<A> predicate;

        public NodeConnection(Node<A, ?> node) {
            this(node, t -> { return true; });
        }

        public NodeConnection(Node<A, ?> node, Predicate<A> predicate) {
            logger.info(String.format("New NodeConnection: %s -> %s.", Node.this.getIdentifier(), node.getIdentifier()));
            this.node = node;
            this.predicate = predicate;
        }
    }
}
