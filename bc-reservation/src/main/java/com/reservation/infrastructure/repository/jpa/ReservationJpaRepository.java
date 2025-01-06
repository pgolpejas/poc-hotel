package com.reservation.infrastructure.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import com.reservation.domain.model.Reservation;
import com.reservation.infrastructure.repository.entity.ReservationEntity;
import jakarta.persistence.LockModeType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {

  @Query("""
      select case when count(i.id)> 0 then true else false end
      from ReservationEntity i
      where i.id =:id
      """)
  boolean existsByPK(UUID id);

  @Query("""
      select new com.reservation.domain.model.Reservation(r.id
      , r.version
      , r.roomTypeId
      , r.hotelId
      , r.guestId
      , r.start
      , r.end
      , r.status)
      from ReservationEntity r
      where r.id = :id
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Reservation> findAggregateById(UUID id);

  @Modifying
  @Query("delete from ReservationEntity where id = :id")
  void deleteByPK(UUID id);
}
