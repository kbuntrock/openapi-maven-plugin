package com.github.kbuntrock.resources.dto;

import java.util.ArrayList;

public class PageDto<T> extends SliceDto<T> {

    /**
     * Total of available pages
     */
    private int totalPages;

    /**
     * Total elements (addition of all the pages)
     */
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
