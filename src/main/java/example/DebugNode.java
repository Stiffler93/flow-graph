package example;

import flow.Action;
import flow.graph.Node;
import flow.states.State;

import java.util.Collection;
import java.util.function.Function;

public class DebugNode<T> extends Node<T, T> {

    private Function<T, String> method;

    public DebugNode() {
        this(Object::toString);
    }

    public DebugNode(Function<T, String> method) {
        super("Debug");
        this.method = method;
    }

    @Override
    public T execute(T data, State state, Action action) {
        if(data instanceof Collection) {
            ((Collection<T>) data).stream().forEach(d -> System.out.println(method.apply(d)));
        } else {
            System.out.println(method.apply(data));
        }

        return data;
    }
}
