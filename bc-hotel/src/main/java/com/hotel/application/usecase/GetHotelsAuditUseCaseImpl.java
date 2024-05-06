package com.hotel.application.usecase;

import com.hotel.core.infrastructure.database.audit.AuditFilters;
import com.hotel.domain.model.Hotel;
import com.hotel.domain.repository.HotelRepository;
import com.hotel.domain.usecase.GetHotelsAuditUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetHotelsAuditUseCaseImpl implements GetHotelsAuditUseCase {

    private final HotelRepository hotelRepository;

    @Override
    public List<Hotel> getHotelsAudit(final AuditFilters filters, final int limit) {
        return this.hotelRepository.findAuditByFilters(filters, limit);
    }
}
