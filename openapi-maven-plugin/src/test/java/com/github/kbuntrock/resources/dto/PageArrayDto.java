package com.github.kbuntrock.resources.dto;

public class PageArrayDto<T> extends ArrayDto<T> {

    private int totalPages;

    private long totalElements;

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
