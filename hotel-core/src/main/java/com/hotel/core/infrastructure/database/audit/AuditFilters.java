package com.hotel.core.infrastructure.database.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "entity id")
    private UUID id;

    // 2024-03-31T09:14:17.790Z default without format
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(type = "string", pattern = "dd/MM/yyyy HH:mm:ss",
            description = "date in format dd/MM/yyyy HH:mm:ss",
            example = "01/01/2024 09:10:01")
    private LocalDateTime from;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Schema(type = "string", pattern = "dd/MM/yyyy HH:mm:ss",
            description = "date in format dd/MM/yyyy HH:mm:ss",
            example = "31/12/2050 23:59:59")
    private LocalDateTime to;
}
