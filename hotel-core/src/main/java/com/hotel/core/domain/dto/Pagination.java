package com.hotel.core.domain.dto;

import lombok.Builder;

@Builder
public record Pagination(

        int limit,
        int page,
        Long total){
}
