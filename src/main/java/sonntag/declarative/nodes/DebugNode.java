package sonntag.declarative.nodes;

import sonntag.declarative.AbstractNode;
import sonntag.declarative.states.State;

import java.util.Collection;
import java.util.function.Function;

public class DebugNode<T> extends AbstractNode<T, T> {

    private Function<T, String> method;

    public DebugNode() {
        this(Object::toString);
    }

    public DebugNode(Function<T, String> method) {
        super("Debug");
        this.method = method;
    }

    @Override
    public T execute(T data, State state) {
        if(data instanceof Collection) {
            ((Collection<T>) data).stream().forEach(d -> print(method.apply(d)));
        } else {
            print(method.apply(data));
        }

        return data;
    }

    private void print(String s) {
        System.out.println(String.format("%s: %s", getClass().getSimpleName(), s));
    }
}
