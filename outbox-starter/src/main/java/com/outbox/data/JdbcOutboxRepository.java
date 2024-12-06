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

	protected static final String STREAM_OLDER_THAN = "streamOlderThan with query:{}";
	protected static final String TABLE_NAME = "$table_name";

	protected final JdbcTemplate jdbcTemplate;

	protected RowMapper<E> entityRowMapper;

	protected RowMapper<AggregateResume> groupRowMapper;

	protected JDBCQueryConfiguration queryConfiguration;

	protected BaseTableDescriptor baseTableDescriptor;

	protected JdbcOutboxRepository(JdbcTemplate jdbcTemplate, JDBCQueryConfiguration queryConfiguration,
			BaseTableDescriptor e) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityRowMapper = this.getMapper();
		this.queryConfiguration = queryConfiguration;
		this.baseTableDescriptor = e;
		this.groupRowMapper = this.getGroupMapper();
	}

	protected JdbcOutboxRepository(DataSource dataSource, JDBCQueryConfiguration queryConfiguration,
			BaseTableDescriptor e) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.entityRowMapper = this.getMapper();
		this.queryConfiguration = queryConfiguration;
		this.baseTableDescriptor = e;
		this.groupRowMapper = this.getGroupMapper();
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

		String query = this.queryConfiguration.getUpdateQuery();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		final List<String> names = new ArrayList<>();
		final List<Object> values = new ArrayList<>();

		this.baseTableDescriptor.getDescriptors().forEach(baseDesc -> {
			if (baseDesc.isUpdatable()) {
				final String set = baseDesc.getJdbcColumnName() + " =  ?";
				names.add(set);
				values.add(this.invokeGetter(snapshotEntity, baseDesc.getFieldName(), baseDesc.getType(), log));
			}
		});

		query = query.replace("$values", String.join(",", names));
		values.add(snapshotEntity.getId().toString());

		this.jdbcTemplate.update(query, values.toArray());
	}

	private boolean exists(final E snapshotEntity) {

		final String query = this.queryConfiguration.getSelectCountQuery().replace(TABLE_NAME,
				this.baseTableDescriptor.getTableName());

		final Integer exists = this.jdbcTemplate.query(query, rs -> {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return null;
		}, snapshotEntity.getId().toString());
		return exists != null && exists > 0;
	}

	private void add(final E snapshotEntity) {

		String query = this.queryConfiguration.getInsertQuery();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		final List<String> columnNames = new ArrayList<>();
		final List<String> values = new ArrayList<>();

		final List<Object> args = new ArrayList<>();

		this.baseTableDescriptor.getDescriptors().forEach(baseDesc -> {
			columnNames.add(baseDesc.getJdbcColumnName());
			values.add("?");
			args.add(this.invokeGetter(snapshotEntity, baseDesc.getFieldName(), baseDesc.getType(), log));
		});
		query = query.replace("$column_list", String.join(",", columnNames));
		query = query.replace("$values", String.join(",", values));
		this.jdbcTemplate.update(query, args.toArray());
	}

	@Override
	public Long countAll() {

		String query = this.queryConfiguration.getCountAll();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.queryForObject(query, Long.class);
	}

	@Override
	public List<E> findNotPublished() {

		String query = this.queryConfiguration.getFindNotPublishedQuery();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.query(query, this.entityRowMapper);
	}

	@Override
	public List<E> findByAggregateIdAndAggregateType(String id, String type) {

		String query = this.queryConfiguration.getFindByAggregateIdAndAggregateType();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.query(query, this.entityRowMapper, id, type);
	}

	@Override
	public List<AggregateResume> findDistinctAggregates() {
		String query = this.queryConfiguration.getFindAvailableRootData();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.query(query, this.groupRowMapper);
	}

	@Override
	public List<E> findById(String id) {

		String query = this.queryConfiguration.getFindById();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.query(query, this.entityRowMapper, id);
	}

	@Override
	public boolean exists(String id) {
		String query = this.queryConfiguration.getCountById();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		return this.jdbcTemplate.queryForObject(query, Long.class, id) > 0;
	}

	@Override
	public List<E> findByAggregateId(String aggregateId) {

		String query = this.queryConfiguration.getFindByAggregateId();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		return this.jdbcTemplate.query(query, this.entityRowMapper, aggregateId);
	}

	@Override
	public Stream<E> findByAggregateRoot(String aggregateRoot) {
		String query = this.queryConfiguration.getFindByAggregateRoot();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		return this.jdbcTemplate.queryForStream(query, this.entityRowMapper, aggregateRoot);

	}

	@Override
	public E findLatestByAggregateRoot(String aggregateRoot) {

		String query = this.queryConfiguration.getFindLatestByAggregateRoot();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		final List<E> elements = this.jdbcTemplate.query(query, this.entityRowMapper, aggregateRoot);
		return elements.stream().findFirst().orElse(null);

	}

	public int removeOlderThan(final Integer maxWait) {
		final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
		final Timestamp timestamp = Timestamp.from(to);

		final int[] argTypes = {Types.TIMESTAMP};
		final List<Object[]> batchArgs = new ArrayList<>();
		batchArgs.add(new Object[]{timestamp});

		String query = this.queryConfiguration.getRemoveOlderThan();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		final int[] result = this.jdbcTemplate.batchUpdate(query, batchArgs, argTypes);

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

		String query = this.queryConfiguration.getCountOlderThan();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		log.debug(STREAM_OLDER_THAN, query);

		return this.jdbcTemplate.queryForObject(query, Long.class, timestamp);

	}

	@Override
	public Stream<E> streamOlderThan(final Integer maxWait, Integer limit) {
		final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
		final Timestamp timestamp = Timestamp.from(to);

		String query = this.queryConfiguration.getStreamOlderThan();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		log.debug(STREAM_OLDER_THAN, query);
		return this.jdbcTemplate.queryForStream(query, this.entityRowMapper, timestamp);

	}

	@Override
	public List<E> findOlderThan(final Integer maxWait, Integer limit) {
		final Instant to = Instant.now().minus(maxWait, ChronoUnit.DAYS);
		final Timestamp timestamp = Timestamp.from(to);

		String query = this.queryConfiguration.getFindOlderThan();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		query = query.replace("$limit", limit + "");

		log.debug(STREAM_OLDER_THAN, query);
		return this.jdbcTemplate.query(query, this.entityRowMapper, timestamp);

	}

	@Override
	public List<E> pageAll(Integer offset, Integer limit) {

		String query = this.queryConfiguration.getFindAll();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		query = query.replace("$limit", limit + "");
		query = query.replace("$offset", offset + "");
		log.debug(STREAM_OLDER_THAN, query);
		return this.jdbcTemplate.query(query, this.entityRowMapper);

	}

	@Override
	public Stream<E> streamAll() {

		String query = this.queryConfiguration.getStreamAll();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		log.debug(STREAM_OLDER_THAN, query);
		return this.jdbcTemplate.queryForStream(query, this.entityRowMapper);

	}

	public Stream<E> findByAggregateTypeBetweenDates(Instant from, Instant to, String parent) {

		final Timestamp timestampto = Timestamp.from(to);
		final Timestamp timestamfrom = Timestamp.from(from);
		String query = this.queryConfiguration.getFindByAggregateBetweenDates();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		return this.jdbcTemplate.queryForStream(query, this.entityRowMapper, timestamfrom, timestampto, parent);

	}

	public List<E> findPendingByAggregateTypeOlderThan(String aggregateid, List<String> eventIds, String type,
			Instant to) {

		final Timestamp timestamp = Timestamp.from(to);

		String query = this.queryConfiguration.getFindPendingByAggregateTypeOlderThan();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());

		final String inSql = String.join(",", Collections.nCopies(eventIds.size(), "?"));
		query = query.replace("$multiple_ids", inSql);
		final List<Object> args = new ArrayList<>();
		args.add(type);
		args.add(aggregateid);
		args.addAll(eventIds);
		args.add(timestamp);
		final List<E> pending = this.jdbcTemplate.query(query, this.entityRowMapper, args.toArray());

		log.debug(
				"Try to find pending non published outbox entity until: {} result is: {} pending outbox messages to send",
				timestamp, pending.size());
		return pending;

	}

	@Override
	public List<E> findFirstNotPublishedSince(final Long maxWait) {
		final Instant to = Instant.now().minusMillis(maxWait);
		final Timestamp timestamp = Timestamp.from(to);
		String query = this.queryConfiguration.getFindFirstNotPublishedSince();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		final List<E> pending = this.jdbcTemplate.query(query, this.entityRowMapper, timestamp);
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
					final AggregateResume agres = new AggregateResume();
					agres.setAggregate(rs.getString(2));
					agres.setCount(rs.getInt(1));
					return agres;
				} catch (final Exception e) {
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
					final E entity = JdbcOutboxRepository.this.instantiateNew();

					JdbcOutboxRepository.this.baseTableDescriptor.getDescriptors().stream().forEach(field -> {
						Object val = null;
						try {
							val = rs.getObject(field.getJdbcColumnName(), field.getType());
						} catch (final Exception e) {

							if (e.getMessage().equals("conversion to class [B from bytea not supported")) {
								try {
									val = rs.getObject(field.getJdbcColumnName());
								} catch (final SQLException e1) {
									log.error("Error:{} while getting vale from field:{}", e.getMessage(),
											field.getJdbcColumnName());
								}
							} else {
								log.error("Error:{} while getting vale from field:{}", e.getMessage(),
										field.getJdbcColumnName());
							}
						}
						JdbcOutboxRepository.this.invokeSetter(entity, field.getFieldName(), val, log);
					});

					return entity;
				} catch (final Exception e) {
					log.error("Error while mapping rows from table", e);
					return null;
				}

			}
		};
	}

	@Override
	public void removeAll() {

		String query = this.queryConfiguration.getRemoveAll();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		this.jdbcTemplate.update(query);
	}

	@Override
	public void remove(E entity) {
		final int[] argTypes = {};
		final List<Object[]> batchArgs = new ArrayList<>();
		batchArgs.add(new Object[]{entity.getId().toString()});
		String query = this.queryConfiguration.getRemoveItem();
		query = query.replace(TABLE_NAME, this.baseTableDescriptor.getTableName());
		this.jdbcTemplate.batchUpdate(query, batchArgs, argTypes);

	}

}
