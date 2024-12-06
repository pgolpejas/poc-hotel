package com.outbox.stream.postprocessors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.BaseEventEntity;
import com.outbox.data.OutboxRepository;
import com.outbox.data.OutboxRuntimeException;
import com.outbox.stream.EventPublisherResult;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Slf4j
@SuppressWarnings("javaarchitecture:S7091")
public class OutboxEntityPostProcessorImpl implements OutboxEntityPostProcessor {

	private static final String PROCESSING_FOR_ENTITY_WAS_FAILED_EVENT_NOT_SEND = "Processing for entity {} was failed: {}, event not send";

	private final OutboxRepositoryFactory outboxRepositoryFactory;

	private final OutboxConfiguration configuration;

	private final ObjectMapper mapper;

	private final KafkaAvroDeserializer deserializer;

	private final StreamBridge streamBridge;

	@Override
	@Transactional
	public boolean process(final List<BaseEventEntity> sourceEntities, final boolean checkOlderEvents)
			throws OutboxRuntimeException {
		final Map<String, Boolean> results;
		final List<String> ids = sourceEntities.stream().map(ent -> ent.getId().toString()).toList();
		try {
			results = this.processForEach(checkOlderEvents, sourceEntities);
		} catch (final Exception me) {
			throw new OutboxRuntimeException("Transactional process for entities {} failed , event not send:" + ids,
					me);
		}

		return !results.containsValue(false);
	}

	@SuppressWarnings({"unchecked"})
	private Map<String, Boolean> processForEach(final boolean checkOlderEvents, List<BaseEventEntity> sourceEntities)
			throws JsonProcessingException {
		final Map<String, Boolean> results = new HashMap<>();

		boolean continueProcessing = true;
		final BaseEventEntity entity;
		if (checkOlderEvents) {
			final List<String> ids = sourceEntities.stream().map(ent -> ent.getId().toString()).toList();
			entity = sourceEntities.getFirst();
			log.info("Check older events than:{}", ids);
			// Only check the first event, because all other events are for the same
			// aggregate and transaction
			final List<BaseEventEntity> entities = this.outboxRepositoryFactory.resolve(entity.getClass())
					.findPendingByAggregateTypeOlderThan(entity.getAggregateId(), ids, entity.getAggregateRoot(),
							entity.getCreatedAt());

			if (!entities.isEmpty()) {
				log.debug(PROCESSING_FOR_ENTITY_WAS_FAILED_EVENT_NOT_SEND, entity.getId(), "Older event present");
				entities.forEach(ent -> log.debug(
						"--> Error on processing Outbox with id:{} Older event present with id:{}, created_on:{}",
						entity.getId().toString(), ent.getId(), ent.getCreatedAt()));
				results.put(entity.getId().toString(), false);
				continueProcessing = false;
			}

		}
		if (continueProcessing) {
			boolean continueSend = true;
			for (final BaseEventEntity partialEnt : sourceEntities) {
				if (continueSend) {
					// The moment one fails, the sending is stopped, and they are queued for the
					// scheduler.
					continueSend = this.continueProcessingSend(partialEnt);
				}
				results.put(partialEnt.getId().toString(), continueSend);
			}
		}
		return results;

	}

	private boolean continueProcessingSend(BaseEventEntity entity) throws JsonProcessingException {

		final StopWatch watch = new StopWatch();

		final SpecificRecord envelope = (SpecificRecord) this.deserializer.deserialize("", entity.getSerializedEvent());

		// Add headers
		final Map<String, Object> headers = entity.getHeadersMap() != null
				? this.mapper.readValue(entity.getHeadersMap(), new TypeReference<>() {
				})
				: new HashMap<>();
		final Message<SpecificRecord> msgToSend = MessageBuilder.createMessage(envelope, new MessageHeaders(headers));

		final boolean send = this.streamBridge.send(this.getBindingName(entity, envelope.getClass().getName()),
				msgToSend);
		log.info("Entity {} send {}", entity.getId(), send);

		final EventPublisherResult publishResult = EventPublisherResult.builder().retryable(true).successful(send)
				.build();
		return this.processResult(publishResult, entity, watch);
	}

	private String getBindingName(BaseEventEntity entity, String envelopeClassName) {
		log.info("Try to find binding for class:" + entity.getAggregateType());

		final OutboxConfiguration.EventBinding bind = this.configuration.getEventBinding().get(envelopeClassName);
		if (bind == null) {
			final String message = String.format(
					"Processing for entity %s was failed, event not send: %s there is no binding configuration for event type: %s",
					entity, envelopeClassName, entity.getAggregateType());
			log.error(message);
			throw new OutboxRuntimeException(message);
		} else {
			return bind.getBindingName();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected boolean processResult(EventPublisherResult publishResult, BaseEventEntity entityToSave, StopWatch watch) {

		final OutboxRepository outboxRepository = this.getOutboxRepositoryFactory().resolve(entityToSave.getClass());

		if (publishResult.isSuccessful()) {

			watch.start();
			if (this.getConfiguration().isDeleteEventOnSend(entityToSave.getClass())) {
				log.info("Delete event on send:{}", entityToSave);
				outboxRepository.remove(entityToSave);
			} else {
				entityToSave.publish();
				log.info("Mark as published event on send {}", entityToSave);
				outboxRepository.save(entityToSave);
			}
			watch.stop();
			log.info("Sent event: {} in {} mils", entityToSave.getAggregateId(), watch.getTime());
			return true;
		} else {
			log.info("Event not send: {}", entityToSave);
			if (publishResult.isRetryable()) {
				entityToSave.retry(publishResult.getErrorMessage());
			} else {
				entityToSave.doNotRetry(publishResult.getErrorMessage());
				entityToSave.setRetryable(false);
			}
			outboxRepository.save(entityToSave);
			return false;
		}
	}

}
