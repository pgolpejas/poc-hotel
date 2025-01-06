package com.reservation.infrastructure.repository.mapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hotel.core.infrastructure.database.helper.JsonQueryHelper.JsonQueryDto;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AggregatedReservationCriteriaMapper {

  public static List<JsonQueryDto> mapToJsonQueryDto(final AggregatedReservationCriteria filters) {
    if (filters == null) {
      return List.of();
    }

    final List<JsonQueryDto> jsonQueryDtoList = new ArrayList<>();
    jsonQueryDtoList.add(JsonQueryDto.builder()
        .filter(CollectionUtils.isEmpty(filters.guestIds()) ? new LinkedHashSet<>()
            : filters.guestIds().stream()
                .map(filter -> new LinkedHashSet<>(Set.of(filter.toString())))
                .collect(Collectors.toCollection(LinkedHashSet::new)))
        .pattern("\"guestId\": %s")
        .build());

    jsonQueryDtoList.add(JsonQueryDto.builder()
        .filter(CollectionUtils.isEmpty(filters.roomTypeIds()) ? new LinkedHashSet<>()
            : filters.roomTypeIds().stream()
                .map(filter -> new LinkedHashSet<>(Set.of(filter.toString())))
                .collect(Collectors.toCollection(LinkedHashSet::new)))
        .pattern("\"roomTypeId\": %s")
        .isNotString(true)
        .build());

    jsonQueryDtoList.add(JsonQueryDto.builder()
        .filter(CollectionUtils.isEmpty(filters.hotelCountries()) ? new LinkedHashSet<>()
            : filters.hotelCountries().stream()
                .map(filter -> new LinkedHashSet<>(Set.of(filter)))
                .collect(Collectors.toCollection(LinkedHashSet::new)))
        .pattern("\"hotel\": %s")
        .subpattern("{\"country\": %s}")
        .build());

    return jsonQueryDtoList.stream()
        .filter(jsonQueryDto -> jsonQueryDto.filter() != null && !jsonQueryDto.filter().isEmpty()).toList();
  }
}
