package sonntag.declarative;

import sonntag.declarative.states.CleanState;
import sonntag.declarative.states.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class AbstractNode<T, V> implements Node<T, V> {

    private static Logger logger = Logger.getLogger(AbstractNode.class.getName());

    private final UUID uuid = UUID.randomUUID();
    private final String ID;
    private final List<NodeConnection<V>> nodeConnections;
    private State state;

    public AbstractNode(String id) {
        this(id, new CleanState());
    }

    public AbstractNode(String id, State state) {
        logger.finer(String.format("Created: %s(\"%s\", id: %s, State: %s", getClass().getSimpleName(), uuid.toString(), id, state.getIdentifier()));

        ID = id;
        nodeConnections = new ArrayList<>(5);
        this.state = state;
    }

    protected abstract V execute(T data, State state);

    @Override
    public final void then(Node<V, ?>... node) {
        for(Node<V, ?> n : node) {
            nodeConnections.add(new NodeConnection<>(n));
        }
    }

    @Override
    public final void then(Node<V, ?> node, Predicate<V> predicate) {
        nodeConnections.add(new NodeConnection<>(node, predicate));
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final String getIdentifier() {
        return ID;
    }

    @Override
    public final boolean remove(String id) {
        return nodeConnections.removeIf(nc -> nc.node.getIdentifier().equals(id));
    }

    protected State determineState() {
        return null;
    }

    @Override
    public final List<Task<?, ?>> trigger(T data) {
        logger.info(String.format("%s-trigger: id=%s, input=%s", getClass().getSimpleName(), getIdentifier(), data.getClass().getSimpleName()));
        if(stop())
            return new ArrayList<>();

        State newState = determineState();
        if(newState != null)
            state = newState;

        V result = execute(data, state);
        if(result == null)
            return new ArrayList<>();

        return forward(result);
    }

    protected boolean stop() {
        return false;
    }

    public void reset() {
    }

    @Override
    public final void clean() {
        if(state.getIdentifier().equals(CleanState.ID))
            return;

        reset();
        nodeConnections.stream().forEach(c -> {
            c.node.clean();
        });
    }

    protected final List<Task<?, ?>> forward(V data) {
        List<Task<?, ?>> tasks = new ArrayList<>();
        nodeConnections.stream().forEach(c -> {
            if(c.predicate.test(data))
                tasks.add(Task.of(data, c.node));
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
            logger.info(String.format("New NodeConnection: %s -> %s.", AbstractNode.this.getIdentifier(), node.getIdentifier()));
            this.node = node;
            this.predicate = predicate;
        }
    }
}
