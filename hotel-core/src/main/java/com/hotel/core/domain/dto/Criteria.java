package com.hotel.core.domain.dto;

import lombok.Builder;

@Builder
public record Criteria(
        String filters, int page, int limit,  String sortDirection, String sortBy) {

}
