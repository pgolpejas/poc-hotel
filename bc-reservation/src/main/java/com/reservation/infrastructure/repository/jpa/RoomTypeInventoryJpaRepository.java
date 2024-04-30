package com.reservation.infrastructure.repository.jpa;

import com.reservation.domain.model.RoomTypeInventory;
import com.reservation.infrastructure.entity.RoomTypeInventoryEntity;
import jakarta.persistence.LockModeType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@JaversSpringDataAuditable
public interface RoomTypeInventoryJpaRepository extends JpaRepository<RoomTypeInventoryEntity, UUID> {

    @Query("""
            select case when count(i.id)> 0 then true else false end
            from RoomTypeInventoryEntity i
            where i.id =:id
            """)
    boolean existsByPK(UUID id);

    @Query("""
            select case when count(i.id)> 0 then true else false end
            from RoomTypeInventoryEntity i
            where i.hotelId = :hotelId
              and i.roomTypeId = :roomTypeId
              and i.roomTypeInventoryDate = :roomTypeInventoryDate
            """)
    boolean existsByUK(UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate);

    @Query("""
            select new com.reservation.domain.model.RoomTypeInventory(r.id
            , r.version
            , r.roomTypeId
            , r.hotelId
            , r.roomTypeInventoryDate
            , r.totalInventory
            , r.totalReserved)
            from RoomTypeInventoryEntity r
            where r.id = :id
              """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomTypeInventory> findAggregateById(UUID id);

    @Modifying
    @Query("delete from RoomTypeInventoryEntity where id = :id")
    void deleteByPK(UUID id);

    @Query("""
            select new com.reservation.domain.model.RoomTypeInventory(r.id
            , r.version
            , r.roomTypeId
            , r.hotelId
            , r.roomTypeInventoryDate
            , r.totalInventory
            , r.totalReserved)
            from RoomTypeInventoryEntity r
            where r.hotelId = :hotelId
              and r.roomTypeId = :roomTypeId
              and r.roomTypeInventoryDate = :roomTypeInventoryDate
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomTypeInventory> findByUK(UUID hotelId, Integer roomTypeId, LocalDate roomTypeInventoryDate);


    @Query("""
            select new com.reservation.domain.model.RoomTypeInventory(r.id
            , r.version
            , r.roomTypeId
            , r.hotelId
            , r.roomTypeInventoryDate
            , r.totalInventory
            , r.totalReserved)
            from RoomTypeInventoryEntity r
            where r.hotelId = :hotelId
              and r.roomTypeId = :roomTypeId
              and r.roomTypeInventoryDate between :start and :end
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<RoomTypeInventory> findByReservationDates(UUID hotelId, Integer roomTypeId, LocalDate start, LocalDate end);
}
