package io.github.kbuntrock.resources.dto;

public class ArrayDto<T> {

	private T[] content;

	private boolean hasNext;

	public T[] getContent() {
		return content;
	}

	public void setContent(final T[] content) {
		this.content = content;
	}

	public boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(final boolean hasNext) {
		this.hasNext = hasNext;
	}
}
