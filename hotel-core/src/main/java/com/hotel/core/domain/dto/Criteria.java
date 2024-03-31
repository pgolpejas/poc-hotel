package com.hotel.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record Criteria(
        @Schema(type = "string", 
                example = "id:'d1a97f69-7fa0-4301-b498-128d78860828'", 
                defaultValue = "id:'d1a97f69-7fa0-4301-b498-128d78860828'"
                , description = "Spring filter expression")
        String filters,

        @Schema(type = "integer", minimum = "0", example = "0", defaultValue = "0", description = "pagination page number starts from 0")
        int page,

        @Schema(type = "integer", example = "10", defaultValue = "10", description = "maximum number of items to return")
        int limit,

        @Schema(type = "string", example = "ASC", defaultValue = "ASC", description = "sort direction ASC|DESC")
        String sortDirection,

        @Schema(type = "string", example = "id", defaultValue = "id", description = "sort by field")
        String sortBy) {

}
