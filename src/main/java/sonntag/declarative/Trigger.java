package sonntag.declarative;

import java.util.List;

public interface Trigger<T> {

    List<Task<?>> trigger(T data);
}
