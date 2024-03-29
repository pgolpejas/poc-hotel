package com.pochotel.reservation.adapters.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pochotel.shared.validation.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ValidDateRange
public record HotelAvailabilitySearchDto(

    @NotEmpty @Size(max = 7) @Pattern(regexp = "\\d{4}[A-Za-z]{3}") @Schema(example = "1234Abd") String hotelId,

    @NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy") @Schema(type = "string", pattern = "dd/MM/yyyy",
                                                                          description = "date in format dd/MM/yyyy",
                                                                          example = "01/07/2024") LocalDate checkIn,

    @NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy") @Schema(type = "string", pattern = "dd/MM/yyyy",
                                                                          description = "date in format dd/MM/yyyy",
                                                                          example = "30/07/2024") LocalDate checkOut,

    @NotEmpty @Size(max = 10) @Schema(example = "[1,4,34,35]") List<@Range(min = 0, max = 120) Integer> ages) {

    public HotelAvailabilitySearchDto(@NotEmpty @Size(max = 7) @Pattern(regexp = "\\d{4}[A-Za-z]{3}") @Schema(example = "1234Abd")
    String hotelId,
        @NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy", description = "date in format dd/MM/yyyy", example = "01/07/2023") LocalDate checkIn,
        @NotNull @FutureOrPresent @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy", description = "date in format dd/MM/yyyy", example = "30/07/2023") LocalDate checkOut,
        @NotEmpty @Size(max = 10) @Schema(example = "[1,4,34,35]") List<@Range(min = 0, max = 120) Integer> ages) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        if (Objects.nonNull(ages)) {
            this.ages = Collections.unmodifiableList(ages);
        } else {
            this.ages = null;
        }

    }
}

