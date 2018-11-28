package flow.graph;

import flow.Action;

import java.util.List;

public interface Trigger<T> {

    List<Task<?>> trigger(T data, Action action);
}
