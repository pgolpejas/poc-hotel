package com.hotel.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record Pagination(

        @Schema(type = "integer", example = "10", defaultValue = "10", description = "maximum number of items to return")
        int limit,

        @Schema(type = "integer", minimum = "0", example = "0", defaultValue = "0", description = "pagination page number starts from 0")
        int page,

        @Schema(type = "integer", minimum = "0", description = "total elements", defaultValue = "10")
        Long total){
}
