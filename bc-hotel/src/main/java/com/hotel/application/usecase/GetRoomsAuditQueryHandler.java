package com.hotel.application.usecase;

import java.util.List;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.GetRoomsAuditQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetRoomsAuditQueryHandler implements GetRoomsAuditQuery {

  private final RoomRepository repository;

  @Override
  public List<Room> getRoomsAudit(final AuditFilters filters, final int limit) {
    return this.repository.findAuditByFilters(filters, limit);
  }
}
