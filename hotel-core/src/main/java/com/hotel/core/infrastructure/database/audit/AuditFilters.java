package com.hotel.core.infrastructure.database.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AuditFilters {
    private UUID id;
    private LocalDateTime from;
    private LocalDateTime to;
}
