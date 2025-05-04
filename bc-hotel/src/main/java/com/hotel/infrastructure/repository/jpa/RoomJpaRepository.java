package com.hotel.infrastructure.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import com.hotel.domain.model.Room;
import com.hotel.infrastructure.entity.RoomEntity;
import jakarta.persistence.LockModeType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface RoomJpaRepository extends JpaRepository<RoomEntity, UUID> {

  @Query("""
      select case when count(i.id)> 0 then true else false end
      from RoomEntity i
      where i.id =:id
      """)
  boolean existsByPK(UUID id);

  @Query("""
      select case when count(r.id)> 0 then true else false end
      from RoomEntity r
      where r.hotelId = :hotelId
        and r.roomTypeId = :roomTypeId
        and r.floor = :floor
        and r.roomNumber = :roomNumber
      """)
  boolean existsByUK(UUID hotelId, Integer roomTypeId, int floor, String roomNumber);

  @Query("""
      select new com.hotel.domain.model.Room(r.id, r.version, r.roomTypeId, r.hotelId, r.name, r.floor, r.roomNumber, r.available)
      from RoomEntity r
      where r.id = :id
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Room> findAggregateById(UUID id);

  @Modifying
  @Query("delete from RoomEntity where id = :id")
  void deleteByPK(UUID id);

  @Query("""
      select r
      from RoomEntity r
      where r.hotelId = :hotelId
        and r.roomTypeId = :roomTypeId
        and r.floor = :floor
        and r.roomNumber = :roomNumber
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Room> findByUK(UUID hotelId, Integer roomTypeId, int floor, String roomNumber);
}
