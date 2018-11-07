package flow.graph;

import flow.Action;
import flow.DefaultAction;

public abstract class Graph {

    public static<T> void start(Node<T,?> node, T data) {
        start(node, data, new DefaultAction());
    }

    public static<T> void start(Node<T,?> node, T data, Action action) {
        node.trigger(data, action);
    }

    public static<T> void clean(Node<T, ?> node) {
        node.clean();
    }

    public static<T> void start(Endpoint<T, ?> endpoint) {
        start(endpoint, true);
    }

    public static<T> void start(Endpoint<T, ?> endpoint, boolean blocking) {
        endpoint.initialize();
    }
}
