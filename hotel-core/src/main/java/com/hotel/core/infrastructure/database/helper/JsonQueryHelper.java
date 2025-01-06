package com.hotel.core.infrastructure.database.helper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.With;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class JsonQueryHelper {

  public static Set<Set<String>> buildFilterRelated(final Set<Set<String>> filterDependency,
      final List<JsonQueryDto> jsonQueryDtoList) {
    if (CollectionUtils.isEmpty(jsonQueryDtoList)) {
      return filterDependency;
    }

    final JsonQueryDto currentJsonQueryDto = jsonQueryDtoList.getFirst();
    final List<String> currentFilter = currentJsonQueryDto.filter().stream()
        .map(filters -> String.format(currentJsonQueryDto.pattern(), filters.stream()
            .map(filter -> (StringUtils.hasText(currentJsonQueryDto.subpattern())
                ? String.format(currentJsonQueryDto.subpattern, parseFilter(currentJsonQueryDto.isNotString(), filter))
                : parseFilter(currentJsonQueryDto.isNotString(), filter)))
            .collect(Collectors.joining(","))))
        .toList();

    final Set<Set<String>> filterDependencyNew = new LinkedHashSet<>();
    if (filterDependency.isEmpty()) {
      filterDependencyNew.addAll(currentFilter.stream()
          .map(filter -> new LinkedHashSet<>(Set.of(filter)))
          .collect(Collectors.toCollection(LinkedHashSet::new)));
    } else {
      filterDependency.forEach(filterDependencyList -> currentFilter.forEach(currentFilterElement -> {
        final Set<String> dependencyNew = new LinkedHashSet<>(filterDependencyList);
        dependencyNew.add(currentFilterElement);
        filterDependencyNew.add(dependencyNew);
      }));
    }
    if (jsonQueryDtoList.size() > 1) {
      return buildFilterRelated(filterDependencyNew, jsonQueryDtoList.subList(1, jsonQueryDtoList.size()));
    } else {
      return filterDependencyNew;
    }
  }

  private static String parseFilter(boolean isNotString, String filter) {
    if (isNotString) {
      return filter;
    }
    return "\"" + filter + "\"";
  }

  @Builder
  @With
  public record JsonQueryDto(
      LinkedHashSet<LinkedHashSet<String>> filter,
      String pattern,
      String subpattern,
      boolean isNotString) {
  }
}
