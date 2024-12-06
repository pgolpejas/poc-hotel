package com.outbox.configuration;

import com.outbox.data.SnapshotEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Data
@Slf4j
@SuppressWarnings("javaarchitecture:S7091")
public class OutboxConfiguration {

	private Integer maxTimeForProcess;

	private Integer outboxEntityMaxRetentionTimeInDays;

	private Integer snapshotEntityMaxRetentionTimeInDays;

	private Integer outboxArchivePageSize;

	private Integer snapshotArchivePageSize;

	private Integer outboxMigratesPageSize;

	private Integer snapshotMigratesPageSize;

	private boolean sendEventsJustInTime = true;

	private boolean outboxEntityEnableColdStorage;

	private boolean outboxDeleteSerializedEventOnSend;

	private String outboxSerializer;

	private Map<String, EventBinding> eventBinding;

	@Value("${spring.application.name}")
	protected String serviceName;

	private Long maxTimeWaitForSendInMils;

	private boolean storeRawJson;

	private boolean deleteSnapshotOnSend = false;

	private String mode;

	private String repositoryType;

	private boolean enableSnapshotSend;
	private boolean enableOutboxSend;
	private boolean enableSchedule;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class EventBinding {

		String bindingName;

	}

	public String getBindingName(String envelopeClassName) {
		final OutboxConfiguration.EventBinding bind = this.getEventBinding().get(envelopeClassName);
		if (bind != null) {
			return bind.getBindingName();
		}
		return null;
	}

	public boolean isDeleteEventOnSend(Class<?> entity) {
		if (entity.equals(SnapshotEntity.class)) {
			return this.isDeleteSnapshotOnSend();
		}
		return false;

	}

}
