package com.hotel.core.infrastructure.database.audit;

import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.shadow.Shadow;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JaversUtil {

    private final JaversSqlRepository javersSqlRepository;

    public <T> List<T> findAuditByInstanceId(final T entity, final AuditFilters filters, final int limit) {

        if (Objects.isNull(entity)) {
            return Collections.emptyList();
        } else {
            final LocalDateTime from = Objects.nonNull(filters.getFrom()) ? filters.getFrom() : LocalDateTime.now().minusYears(50);
            final LocalDateTime to = Objects.nonNull(filters.getTo()) ? filters.getTo() : LocalDateTime.now();

            final Javers javers = JaversBuilder.javers().registerJaversRepository(this.javersSqlRepository).build();
            final QueryBuilder jqlQuery = QueryBuilder
                    .byInstance(entity)
                    .withScopeCommitDeep()
                    .from(from)
                    .to(to)
                    .limit(limit);

            final List<Shadow<T>> shadows = javers.findShadows(jqlQuery.build());
            return shadows.stream()
                    .map(Shadow::get)
                    .toList();
        }
    }
}
