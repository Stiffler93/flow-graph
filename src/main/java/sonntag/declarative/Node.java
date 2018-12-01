package sonntag.declarative;

import sonntag.declarative.states.State;

import java.util.List;
import java.util.function.Predicate;

public interface Node<T, V> extends Trigger<T> {

    void then(Node<V, ?>... node);

    void then(Node<V, ?> node, Predicate<V> predicate);

    State getState();

    String getIdentifier();

    boolean remove(String id);

    List<Task<?>> trigger(T data);

    void clean();
}
