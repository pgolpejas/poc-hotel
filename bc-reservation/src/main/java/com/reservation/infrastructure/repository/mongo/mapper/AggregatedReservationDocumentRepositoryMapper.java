package com.reservation.infrastructure.repository.mongo.mapper;

import com.reservation.domain.model.AggregatedReservation;
import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AggregatedReservationDocumentRepositoryMapper {

  AggregatedReservationDocument mapAggregatedReservationDocument(AggregatedReservation cycleDispatch);

  AggregatedReservation mapAggregatedReservation(AggregatedReservationDocument cycleDispatchDocument);

}
