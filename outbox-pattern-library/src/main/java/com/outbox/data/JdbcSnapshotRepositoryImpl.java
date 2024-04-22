package com.outbox.data;


import com.outbox.configuration.JDBCQueryConfiguration;
import com.outbox.data.tabledescriptors.BaseTableDescriptor;

import javax.sql.DataSource;

public class JdbcSnapshotRepositoryImpl extends JdbcOutboxRepository<SnapshotEntity> {

    public JdbcSnapshotRepositoryImpl(DataSource dataSource, JDBCQueryConfiguration queryConfiguration,
            BaseTableDescriptor e) {
        super(dataSource, queryConfiguration, e);
    }

    @Override
    public boolean canStore(Class<?> eventClass) {
        return SnapshotEntity.class.equals(eventClass);
    }

    @Override
    public SnapshotEntity instantiateNew() {
        return new SnapshotEntity();
    }

}
