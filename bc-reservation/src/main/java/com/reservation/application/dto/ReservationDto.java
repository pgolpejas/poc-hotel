package com.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reservation.application.validator.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@ValidDateRange
public record ReservationDto(

        @NotNull
        UUID id,

        int version,

        @NotNull
        @Positive
        @Schema(type = "string",                example = "1")
        Integer roomTypeId,

        @NotNull
        UUID hotelId,

        @NotNull
        UUID guestId,

        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy",
                description = "date in format dd/MM/yyyy",
                example = "01/07/2024")
        LocalDate start,

        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy",
                description = "date in format dd/MM/yyyy",
                example = "02/07/2024")
        LocalDate end,

        @Size(max = 20)
        @Schema(example = "ON")
        String status) {
}
