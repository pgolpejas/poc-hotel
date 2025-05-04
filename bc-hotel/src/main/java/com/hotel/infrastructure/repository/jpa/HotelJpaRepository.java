package com.hotel.infrastructure.repository.jpa;

import java.util.Optional;
import java.util.UUID;

import com.hotel.domain.model.Hotel;
import com.hotel.infrastructure.entity.HotelEntity;
import jakarta.persistence.LockModeType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@JaversSpringDataAuditable
public interface HotelJpaRepository extends JpaRepository<HotelEntity, UUID> {

  @Query("""
      select case when count(h.id)> 0 then true else false end
      from HotelEntity h
      where h.id =:id
      """)
  boolean existsByPK(UUID id);

  @Query("""
      select case when count(h.id)> 0 then true else false end
      from HotelEntity h
      where h.name = :name
      """)
  boolean existsByUK(String name);

  @Query("""
      select new com.hotel.domain.model.Hotel(h.id, h.version, h.name, h.address, h.city, h.state, h.country, h.postalCode)
      from HotelEntity h
      where h.id = :id
      """)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Hotel> findAggregateById(UUID id);

  @Modifying
  @Query("delete from HotelEntity where id = :id")
  void deleteByPK(UUID id);
}
