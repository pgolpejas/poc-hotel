package com.reservation.infrastructure.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import com.reservation.infrastructure.repository.entity.AggregatedReservationEntity;
import com.reservation.infrastructure.repository.jpa.custom.AggregatedReservationJpaCustomRepository;
import jakarta.persistence.LockModeType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface AggregatedReservationJpaRepository extends JpaRepository<AggregatedReservationEntity, UUID>,
    AggregatedReservationJpaCustomRepository {

  @Query("""
      select case when count(i.id)> 0 then true else false end
      from AggregatedReservationEntity i
      where i.id =:id
      """)
  boolean existsByPK(UUID id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @NotNull
  Optional<AggregatedReservationEntity> findById(@NotNull UUID id);

  @Modifying
  @Query("delete from AggregatedReservationEntity where id = :id")
  void deleteByPK(@NotNull UUID id);
}
