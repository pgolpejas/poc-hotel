package com.hotel.core.application.mapper;

import com.hotel.core.application.dto.CriteriaDto;
import com.hotel.core.domain.dto.Criteria;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring")
public interface CriteriaMapper {

    CriteriaDto mapToDTO(Criteria aggregate);

    Criteria mapToAggregate(CriteriaDto entity);

}
