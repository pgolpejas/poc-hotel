package com.reservation.infrastructure.repository.jpa;

import com.reservation.infrastructure.entity.ReservationEntity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@JaversSpringDataAuditable
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {

}
