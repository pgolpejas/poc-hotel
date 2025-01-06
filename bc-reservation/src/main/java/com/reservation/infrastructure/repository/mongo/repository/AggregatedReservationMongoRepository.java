package com.reservation.infrastructure.repository.mongo.repository;

import java.util.UUID;

import com.reservation.infrastructure.repository.mongo.entity.AggregatedReservationDocument;
import com.reservation.infrastructure.repository.mongo.repository.custom.AggregatedReservationMongoCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedReservationMongoRepository extends MongoRepository<AggregatedReservationDocument, UUID>,
    AggregatedReservationMongoCustomRepository {

}
