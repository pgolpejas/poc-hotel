package com.hotel.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record ListResponse<T>(
        @Schema(description = "items")
        List<T> data) {

}
