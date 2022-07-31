package com.github.kbuntrock.sample.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Buntrock
 */
public class SliceDto<T> {

    private List<T> content;

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
