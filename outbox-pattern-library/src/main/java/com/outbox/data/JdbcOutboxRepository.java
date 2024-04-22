package com.outbox.data;

import com.outbox.configuration.JDBCQueryConfiguration;
import com.outbox.data.tabledescriptors.BaseTableDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class JdbcOutboxRepository<E extends BaseEventEntity> implements OutboxRepository<E> {

    protected static final String TABLE_NAME = "$table_name";

    protected final JdbcTemplate jdbcTemplate;

    protected RowMapper<E> entityRowMapper;

    protected RowMapper<AggregateResume> groupRowMapper;

    protected JDBCQueryConfiguration queryConfiguration;

    protected BaseTableDescriptor baseTableDescriptor;

    protected JdbcOutboxRepository(JdbcTemplate jdbcTemplate, JDBCQueryConfiguration queryConfiguration,
            BaseTableDescriptor e) {
        this.jdbcTemplate = jdbcTemplate;
        entityRowMapper = getMapper();
        this.queryConfiguration = queryConfiguration;
        this.baseTableDescriptor = e;
        groupRowMapper = getGroupMapper();
    }

    protected JdbcOutboxRepository(DataSource dataSource, JDBCQueryConfiguration queryConfiguration,
            BaseTableDescriptor e) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        entityRowMapper = getMapper();
        this.queryConfiguration = queryConfiguration;
        this.baseTableDescriptor = e;
        groupRowMapper = getGroupMapper();
    }

    @Override
    @Transactional
    public void save(final E snapshotEntity) {
        if (this.exists(snapshotEntity)) {
            this.update(snapshotEntity);
        } else {
            this.add(snapshotEntity);
        }
    }

    private void update(final E snapshotEntity) {

        String query = queryConfiguration.getUpdateQuery();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        List<String> names = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        baseTableDescriptor.getDescriptors().forEach(baseDesc -> {
            if (baseDesc.isUpdatable()) {
                String set = baseDesc.getJdbcColumnName() + " =  ?";
                names.add(set);
                values.add(invokeGetter(snapshotEntity, baseDesc.getFieldName(), baseDesc.getType(), log));
            }
        });

        query = query.replace("$values", String.join(",", names));
        values.add(snapshotEntity.getId().toString());

        jdbcTemplate.update(query, values.toArray());
    }

    private boolean exists(final E snapshotEntity) {

        String query = queryConfiguration.getSelectCountQuery();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        Integer exists = jdbcTemplate.queryForObject(query, Integer.class, snapshotEntity.getId().toString());
        return exists != null && exists > 0;
    }

    private void add(final E snapshotEntity) {

        String query = queryConfiguration.getInsertQuery();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        List<String> columnNames = new ArrayList<>();
        List<String> values = new ArrayList<>();

        List<Object> args = new ArrayList<>();

        baseTableDescriptor.getDescriptors().forEach(baseDesc -> {
            columnNames.add(baseDesc.getJdbcColumnName());
            values.add("?");
            args.add(invokeGetter(snapshotEntity, baseDesc.getFieldName(), baseDesc.getType(), log));
        });
        query = query.replace("$column_list", String.join(",", columnNames));
        query = query.replace("$values", String.join(",", values));
        jdbcTemplate.update(query, args.toArray());
    }

    @Override
    public Long countAll() {

        String query = queryConfiguration.getCountAll();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.queryForObject(query, Long.class);
    }

    @Override
    public List<E> findNotPublished() {

        String query = queryConfiguration.getFindNotPublishedQuery();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.query(query, entityRowMapper);
    }

    @Override
    public List<E> findByAggregateIdAndAggregateType(String id, String type) {

        String query = queryConfiguration.getFindByAggregateIdAndAggregateType();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.query(query, entityRowMapper, id, type);
    }

    @Override
    public List<AggregateResume> findDistinctAggregates() {
        String query = queryConfiguration.getFindAvailableRootData();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.query(query, groupRowMapper);
    }

    @Override
    public List<E> findById(String id) {

        String query = queryConfiguration.getFindById();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.query(query, entityRowMapper, id);
    }

    @Override
    public boolean exists(String id) {
        String query = queryConfiguration.getCountById();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        return jdbcTemplate.queryForObject(query, Long.class, id) > 0;
    }

    @Override
    public List<E> findByAggregateId(String aggregateId) {

        String query = queryConfiguration.getFindByAggregateId();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        return jdbcTemplate.query(query, entityRowMapper, aggregateId);
    }

    @Override
    public Stream<E> findByAggregateRoot(String aggregateRoot) {
        String query = queryConfiguration.getFindByAggregateRoot();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        return jdbcTemplate.queryForStream(query, entityRowMapper, aggregateRoot);

    }

    @Override
    public E findLatestByAggregateRoot(String aggregateRoot) {

        String query = queryConfiguration.getFindLatestByAggregateRoot();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        List<E> elements = jdbcTemplate.query(query, entityRowMapper, aggregateRoot);
        return elements.stream().findFirst().orElse(null);

    }

    public int removeOlderThan(final Integer maxWait) {
        final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
        final Timestamp timestamp = Timestamp.from(to);

        int[] argTypes = { Types.TIMESTAMP };
        List<Object[]> batchArgs = new ArrayList<>();
        batchArgs.add(new Object[] { timestamp });

        String query = queryConfiguration.getRemoveOlderThan();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        int[] result = jdbcTemplate.batchUpdate(query, batchArgs, argTypes);

        log.debug("Delete {}  published outbox entity until: {}", result.length, timestamp);
        if (result.length > 0) {
            return result[0];
        } else {
            return 0;
        }
    }

    @Override
    public Long countOlderThan(final Integer maxWait) {
        final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
        final Timestamp timestamp = Timestamp.from(to);

        String query = queryConfiguration.getCountOlderThan();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        log.debug("streamOlderThan with query:{}", query);

        return jdbcTemplate.queryForObject(query, new Object[] { timestamp }, Long.class);

    }

    @Override
    public Stream<E> streamOlderThan(final Integer maxWait, Integer limit) {
        final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
        final Timestamp timestamp = Timestamp.from(to);

        String query = queryConfiguration.getStreamOlderThan();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        log.debug("streamOlderThan with query:{}", query);
        return jdbcTemplate.queryForStream(query, entityRowMapper, timestamp);

    }

    @Override
    public List<E> findOlderThan(final Integer maxWait, Integer limit) {
        final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
        final Timestamp timestamp = Timestamp.from(to);

        String query = queryConfiguration.getFindOlderThan();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        query = query.replace("$limit", limit + "");

        log.debug("streamOlderThan with query:{}", query);
        return jdbcTemplate.query(query, entityRowMapper, timestamp);

    }

    @Override
    public List<E> pageAll(Integer offset, Integer limit) {

        String query = queryConfiguration.getFindAll();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        query = query.replace("$limit", limit + "");
        query = query.replace("$offset", offset + "");
        log.debug("streamOlderThan with query:{}", query);
        return jdbcTemplate.query(query, entityRowMapper);

    }

    @Override
    public Stream<E> streamAll() {

        String query = queryConfiguration.getStreamAll();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        log.debug("streamOlderThan with query:{}", query);
        return jdbcTemplate.queryForStream(query, entityRowMapper);

    }

    public Stream<E> findByAggregateTypeBetweenDates(Instant from, Instant to, String parent) {

        final Timestamp timestampto = Timestamp.from(to);
        final Timestamp timestamfrom = Timestamp.from(from);
        String query = queryConfiguration.getFindByAggregateBetweenDates();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        return jdbcTemplate.queryForStream(query, entityRowMapper, timestamfrom, timestampto, parent);

    }

    public List<E> findPendingByAggregateTypeOlderThan(String aggregateid, List<String> eventIds, String type,
            Instant to) {

        final Timestamp timestamp = Timestamp.from(to);

        String query = queryConfiguration.getFindPendingByAggregateTypeOlderThan();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());

        String inSql = String.join(",", Collections.nCopies(eventIds.size(), "?"));
        query = query.replace("$multiple_ids", inSql);
        List<Object> args = new ArrayList<>();
        args.add(type);
        args.add(aggregateid);
        args.addAll(eventIds);
        args.add(timestamp);
        final List<E> pending = jdbcTemplate.query(query, entityRowMapper, args.toArray());

        log.debug(
                "Try to find pending non published outbox entity until: {} result is: {} pending outbox messages to send",
                timestamp, pending.size());
        return pending;

    }

    @Override
    public List<E> findFirstNotPublishedSince(final Long maxWait) {
        final Instant to = Instant.now().minusMillis(maxWait);
        final Timestamp timestamp = Timestamp.from(to);
        String query = queryConfiguration.getFindFirstNotPublishedSince();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        final List<E> pending = jdbcTemplate.query(query, entityRowMapper, timestamp);
        log.debug(
                "Try to find pending non published outbox entity until: {} result is: {} pending outbox messages to send with query:{}",
                timestamp, pending.size(), query);
        return pending;

    }

    public RowMapper<AggregateResume> getGroupMapper() {
        return new RowMapper<AggregateResume>() {

            @SuppressWarnings("unchecked")
            @Override
            public AggregateResume mapRow(final ResultSet rs, final int rowNum) throws SQLException {

                try {
                    AggregateResume agres = new AggregateResume();
                    agres.setAggregate(rs.getString(2));
                    agres.setCount(rs.getInt(1));
                    return agres;
                } catch (Exception e) {
                    log.error("Error while mapping rows from table", e);
                    return null;
                }

            }
        };
    }

    public RowMapper<E> getMapper() {
        return new RowMapper<E>() {

            @SuppressWarnings("unchecked")
            @Override
            public E mapRow(final ResultSet rs, final int rowNum) throws SQLException {

                try {
                    E entity = instantiateNew();

                    baseTableDescriptor.getDescriptors().stream().forEach(field -> {
                        Object val = null;
                        try {
                            val = rs.getObject(field.getJdbcColumnName(), field.getType());
                        } catch (Exception e) {

                            if (e.getMessage().equals("conversion to class [B from bytea not supported")) {
                                try {
                                    val = rs.getObject(field.getJdbcColumnName());
                                } catch (SQLException e1) {
                                    log.error("Error:{} while getting vale from field:{}", e.getMessage(),
                                            field.getJdbcColumnName());
                                }
                            } else {
                                log.error("Error:{} while getting vale from field:{}", e.getMessage(),
                                        field.getJdbcColumnName());
                            }
                        }
                        invokeSetter(entity, field.getFieldName(), val, log);
                    });

                    return entity;
                } catch (Exception e) {
                    log.error("Error while mapping rows from table", e);
                    return null;
                }

            }
        };
    }

    @Override
    public void removeAll() {

        String query = queryConfiguration.getRemoveAll();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        jdbcTemplate.update(query);
    }

    @Override
    public void remove(E entity) {
        int[] argTypes = {};
        List<Object[]> batchArgs = new ArrayList<>();
        batchArgs.add(new Object[] { entity.getId().toString() });
        String query = queryConfiguration.getRemoveItem();
        query = query.replace(TABLE_NAME, baseTableDescriptor.getTableName());
        jdbcTemplate.batchUpdate(query, batchArgs, argTypes);

    }

}
