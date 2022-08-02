package io.github.kbuntrock.resources.dto;

import java.util.ArrayList;
import java.util.List;

public class SliceDto<T> {

    /**
     * The content of this slice
     */
    private List<T> content;

    /**
     * True if a next slice exist
     */
    private boolean hasNext;

    public static <T> SliceDto<T> emptySlice() {
        final SliceDto<T> slice = new SliceDto<>();
        slice.setContent(new ArrayList<>());
        slice.setHasNext(false);
        return slice;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(final List<T> content) {
        this.content = content;
    }

    public boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(final boolean hasNext) {
        this.hasNext = hasNext;
    }
}
