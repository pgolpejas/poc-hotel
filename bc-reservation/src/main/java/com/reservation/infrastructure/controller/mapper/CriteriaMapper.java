package com.reservation.infrastructure.controller.mapper;

import com.hotel.core.domain.dto.Criteria;
import com.reservation.domain.dto.AggregatedReservationCriteria;
import com.reservation.openapi.model.AggregatedReservationCriteriaDto;
import com.reservation.openapi.model.CriteriaRequestDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "spring")
public interface CriteriaMapper {

  Criteria mapToCriteria(CriteriaRequestDto requestDto);

  AggregatedReservationCriteria mapToAggregateCriteria(AggregatedReservationCriteriaDto requestDto);

}
