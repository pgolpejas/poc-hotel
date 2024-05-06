package com.hotel.application.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Room;
import com.hotel.domain.repository.RoomRepository;
import com.hotel.domain.usecase.GetRoomsAuditUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRoomsAuditUseCaseImpl implements GetRoomsAuditUseCase {

    private final RoomRepository repository;

    @Override
    public List<Room> getRoomsAudit(final AuditFilters filters, final int limit) {
        return this.repository.findAuditByFilters(filters, limit);
    }
}
