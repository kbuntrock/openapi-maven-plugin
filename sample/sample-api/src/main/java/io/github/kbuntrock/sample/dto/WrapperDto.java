package io.github.kbuntrock.sample.dto;

/**
 * @author Kevin Buntrock
 */
public class WrapperDto<T> {

    private final T value;
    private WrapperDto<T> child;

    public WrapperDto(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public WrapperDto<T> getChild() {
        return child;
    }

    public void setChild(WrapperDto<T> child) {
        this.child = child;
    }
}
