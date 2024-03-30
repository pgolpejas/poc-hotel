package com.hotel.core.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record CriteriaDto(
        @NotBlank
        @Schema(description = "spring filter", example = "id:'d1a97f69-7fa0-4301-b498-128d78860828'")
        String filters,

        @PositiveOrZero
        @Schema(description = "pagination page", example = "0", defaultValue = "0")
        Integer page,

        @Positive
        @Schema(description = "pagination limit", example = "10", defaultValue = "10")
        Integer limit,

        @Schema(description = "ASC/DESC", example = "ASC")
        String sortDirection,

        @Schema(description = "sort by field", example = "id")
        String sortBy) {

}
