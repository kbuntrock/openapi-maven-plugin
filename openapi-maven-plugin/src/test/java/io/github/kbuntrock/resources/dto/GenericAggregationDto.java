package io.github.kbuntrock.resources.dto;

/**
 * @author Kevin Buntrock
 */
public class GenericAggregationDto<A, B, C> {

	private PageDto<A> page;
	private SliceDto<B> slice;
	private ArrayDto<C> array;

	public PageDto<A> getPage() {
		return page;
	}

	public void setPage(PageDto<A> page) {
		this.page = page;
	}

	public SliceDto<B> getSlice() {
		return slice;
	}

	public void setSlice(SliceDto<B> slice) {
		this.slice = slice;
	}

	public ArrayDto<C> getArray() {
		return array;
	}

	public void setArray(ArrayDto<C> array) {
		this.array = array;
	}
}
