package com.outbox.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode
@Data
@ToString(exclude = {"serializedEvent", "domainEvent"})
public abstract class BaseEventEntity implements Serializable {

	// outbox primary key
	private UUID id;

	// event creation date
	private Instant createdAt;

	private Instant publishedAt;

	private String aggregateType;

	private String aggregateRoot;

	private String outboxEventType;

	private String sendStrategyType;

	private String aggregateId;

	private String version;

	private byte[] serializedEvent;

	private String headersMap;

	private String errorMessage;

	private Integer retries = 0;

	private boolean retryable = true;

	private String jsonSource;

	private String destinationTopic;

	@Setter
	private transient Object domainEvent;

	public void publish() {
		this.setPublishedAt(Instant.now());
	}

	public void retry(String message) {
		this.setRetries(this.getRetries() + 1);
		this.setRetryable(true);
		this.setErrorMessage(message);
	}

	public void doNotRetry(String message) {
		this.setRetryable(false);
		this.setErrorMessage(message);
	}

}
