package com.hotel.application.usecase;

import java.util.List;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.GetHotelsAuditQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetHotelsAuditQueryHandler implements GetHotelsAuditQuery {

  private final HotelRepository hotelRepository;

  @Override
  public List<Hotel> getHotelsAudit(final AuditFilters filters, final int limit) {
    return this.hotelRepository.findAuditByFilters(filters, limit);
  }
}
