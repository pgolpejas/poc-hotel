package com.outbox.configuration;

import com.outbox.data.*;
import com.outbox.data.tabledescriptors.OutboxEntityTableDescriptor;
import com.outbox.data.tabledescriptors.SnapshotEntityTableDescriptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@AutoConfiguration
@PropertySource({"classpath:outbox-queries.properties"})
public class JDBCTransactionalOutboxAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "outbox")
	public JDBCQueryConfiguration queryConfiguration() {
		return new JDBCQueryConfiguration();
	}

	@Bean("outboxDomainRepository")
	public OutboxRepository<OutboxEntity> jdbcOutboxRepository(final DataSource datasource,
			final JDBCQueryConfiguration queryConfiguration) {
		return new JdbcDomainRepositoryImpl(datasource, queryConfiguration, new OutboxEntityTableDescriptor());
	}

	@Bean("outboxSnapshotRepository")
	// @Lazy
	public OutboxRepository<SnapshotEntity> jdbcSnapshotEntityRepository(final DataSource datasource,
			final JDBCQueryConfiguration queryConfiguration) {
		return new JdbcSnapshotRepositoryImpl(datasource, queryConfiguration, new SnapshotEntityTableDescriptor());
	}

}
