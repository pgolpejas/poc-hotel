package com.reservation.domain.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.hotel.core.application.validator.ScriptExpression;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

@Builder
@With
@ScriptExpression(
    value = "(from != null && to != null) "
        + "? (from.isBefore(to) "
        + "|| from.isEqual(to)) "
        + "&& (from.plusMonths(6).isAfter(to) "
        + "|| from.plusMonths(6).isEqual(to)) : true",
    message = "'from' must be less than or equal to 'to' or 'to' must be less than six months after 'from'")
public record AggregatedReservationCriteria(
    @NotNull LocalDate from,

    @NotNull LocalDate to,

    @Size(max = 100) Set<@NotNull UUID> guestIds,

    @Size(max = 100) Set<@NotNull UUID> hotelIds,

    @Size(max = 100) Set<@NotNull Integer> roomTypeIds,

    @Size(max = 100) Set<@NotNull String> hotelCountries,

    int page,

    int limit,

    @NotBlank String sortDirection,

    // Must be one of the following values: "ASC" or "DESC"
    @NotBlank String sortBy) {
}
