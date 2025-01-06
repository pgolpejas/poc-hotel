package com.hotel.core.infrastructure.database.helper;

import static com.hotel.core.infrastructure.database.helper.JsonQueryHelper.buildFilterRelated;
import static com.reservation.infrastructure.repository.mapper.AggregatedReservationCriteriaMapper.mapToJsonQueryDto;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.reservation.domain.dto.AggregatedReservationCriteria;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
@SuppressWarnings("java:S2133")
class JsonQueryHelperTest {
  @Test
  void when_hotel_countries_not_empty_should_generate_sql() {
    final AggregatedReservationCriteria filters = AggregatedReservationCriteria.builder()
        .hotelCountries(new LinkedHashSet<>(Arrays.asList("Spain", "France")))
        .build();
    final Set<Set<String>> filterDependency = buildFilterRelated(new LinkedHashSet<>(), mapToJsonQueryDto(filters));
    queryLog(filterDependency);
    Assertions.assertThat(filterDependency)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(Set.of(
            Set.of("\"hotel\": {\"country\": \"Spain\"}"),
            Set.of("\"hotel\": {\"country\": \"France\"}")));
  }

  @Test
  void when_room_type_ids_not_empty_should_generate_sql() {
    final Integer roomTypeId1 = 1;
    final Integer roomTypeId2 = 2;
    final AggregatedReservationCriteria filters = AggregatedReservationCriteria.builder()
        .roomTypeIds(new LinkedHashSet<>(Arrays.asList(roomTypeId1, roomTypeId2)))
        .build();
    final Set<Set<String>> filterDependency = buildFilterRelated(new LinkedHashSet<>(), mapToJsonQueryDto(filters));
    queryLog(filterDependency);
    Assertions.assertThat(filterDependency)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(Set.of(
            Set.of(String.format("\"roomTypeId\": %s", roomTypeId1)),
            Set.of(String.format("\"roomTypeId\": %s", roomTypeId2))));
  }

  @Test
  void when_guest_ids_not_empty_should_generate_sql() {
    final String guestId1 = "a1a97f69-7fa0-4301-b498-128d78860828";
    final String guestId2 = "d1a97f69-7fa0-4301-b498-128d78860828";
    final AggregatedReservationCriteria filters = AggregatedReservationCriteria.builder()
        .guestIds(new LinkedHashSet<>(Arrays.asList(UUID.fromString(guestId1), UUID.fromString(guestId2))))
        .build();
    final Set<Set<String>> filterDependency = buildFilterRelated(new LinkedHashSet<>(), mapToJsonQueryDto(filters));
    queryLog(filterDependency);
    Assertions.assertThat(filterDependency)
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(Set.of(
            Set.of(String.format("\"guestId\": \"%s\"", guestId1)),
            Set.of(String.format("\"guestId\": \"%s\"", guestId2))));
  }

  private static void queryLog(Set<Set<String>> filterDependency) {
    final String query = filterDependency.stream()
        .map(filtersList -> "aggregate @> '{\n" + String.join(",\n ", filtersList) + "\n}'")
        .collect(Collectors.joining(" \nOR "));
    log.info("{}: {}", new Object() {}.getClass().getEnclosingMethod().getName(), query);
  }
}
