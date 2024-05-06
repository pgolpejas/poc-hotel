package com.outbox.data;

import com.outbox.configuration.JDBCQueryConfiguration;
import com.outbox.data.tabledescriptors.BaseTableDescriptor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class JdbcDomainRepositoryImpl extends JdbcOutboxRepository<OutboxEntity> {

    public JdbcDomainRepositoryImpl(DataSource dataSource, JDBCQueryConfiguration queryConfiguration,
                                    BaseTableDescriptor e) {
        super(dataSource, queryConfiguration, e);
    }

    public JdbcDomainRepositoryImpl(JdbcTemplate jdbcTemplate, JDBCQueryConfiguration queryConfiguration,
                                    BaseTableDescriptor e) {
        super(jdbcTemplate, queryConfiguration, e);
    }

    @Override
    public boolean canStore(Class<?> eventClass) {
        return OutboxEntity.class.equals(eventClass);
    }

    @Override
    public OutboxEntity instantiateNew() {
        return new OutboxEntity();
    }

}
