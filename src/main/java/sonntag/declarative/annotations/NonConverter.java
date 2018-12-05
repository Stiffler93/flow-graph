package sonntag.declarative.annotations;

class NonConverter<T> implements Converter<T, T> {

    @Override
    public T convert(T input) {
        return input;
    }
}
