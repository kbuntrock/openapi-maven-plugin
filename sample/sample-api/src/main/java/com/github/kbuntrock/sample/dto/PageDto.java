package com.github.kbuntrock.sample.dto;

import java.util.ArrayList;

/**
 * @author Kevin Buntrock
 */
public class PageDto<T> extends SliceDto<T> {

    private int totalPages;

    private long totalElements;

    public static <T> PageDto<T> emptyPage() {
        final PageDto<T> page = new PageDto<>();
        page.setContent(new ArrayList<>());
        page.setTotalPages(0);
        page.setTotalElements(0);
        page.setHasNext(false);
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(final int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(final long totalElements) {
        this.totalElements = totalElements;
    }
}
