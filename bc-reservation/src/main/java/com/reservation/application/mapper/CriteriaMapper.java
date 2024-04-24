package com.reservation.application.mapper;

import com.hotel.core.domain.dto.Criteria;
import com.reservation.openapi.model.CriteriaRequestDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface CriteriaMapper {


    Criteria mapToAggregate(CriteriaRequestDto entity);

}
