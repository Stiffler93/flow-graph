package sonntag.declarative.annotations;

public interface Converter<T, V> {

    V convert(T input);
}
