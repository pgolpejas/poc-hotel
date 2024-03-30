package com.reservation.domain.utils;

import lombok.Builder;

@Builder
public record Criteria(
        String filters, int page, int size, String sortDirection, String sortBy) {

}
