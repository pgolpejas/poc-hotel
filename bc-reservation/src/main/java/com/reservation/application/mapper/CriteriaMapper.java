package com.reservation.application.mapper;

import com.reservation.application.dto.CriteriaDto;
import com.reservation.domain.utils.Criteria;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface CriteriaMapper {

    CriteriaDto mapToDTO(Criteria aggregate);

    Criteria mapToAggregate(CriteriaDto entity);

}
