package com.supercoding.shoppingmallbackend.common.util;

import com.supercoding.shoppingmallbackend.dto.response.PaginationResponse;

import java.util.List;

public class PaginationBuilder<T> {
    private boolean hasNext;
    private boolean hasPrevious;
    private int totalPages;
    private List<T> contents;

    public PaginationResponse<T> build() { return new PaginationResponse<>(hasNext, hasPrevious, totalPages, contents); }

    public PaginationBuilder<T> hasNext(boolean hasNext) {
        this.hasNext = hasNext;
        return this;
    }

    public PaginationBuilder<T> hasPrivious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
        return this;
    }

    public PaginationBuilder<T> totalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public PaginationBuilder<T> contents(List<T> contents) {
        this.contents = contents;
        return this;
    }
}
