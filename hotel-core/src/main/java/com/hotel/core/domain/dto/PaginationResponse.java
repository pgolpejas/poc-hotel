package com.hotel.core.domain.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginationResponse<T>(
        Pagination pagination,
        List<T> data) {

}
