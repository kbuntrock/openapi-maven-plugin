package io.github.kbuntrock.resources.dto;

/**
 * @author Kevin Buntrock
 */
public class WrapperDto<T> {

	private T wrapped;

	public T getWrapped() {
		return wrapped;
	}

	public void setWrapped(T wrapped) {
		this.wrapped = wrapped;
	}
}
