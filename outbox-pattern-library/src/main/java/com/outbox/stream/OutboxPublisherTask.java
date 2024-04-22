package com.outbox.stream;

import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.BaseEventEntity;
import com.outbox.scheduler.ParallelizableService;
import com.outbox.scheduler.ProcessOutboxEntityTask;
import com.outbox.stream.postprocessors.OutboxEntityPostProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class OutboxPublisherTask {

    private final OutboxRepositoryFactory outboxRepositoryFactory;

    private final OutboxConfiguration outboxPatternConfiguration;

    private final OutboxEntityPostProcessor outboxEntityPostProcessor;

    private final ParallelizableService parallelizableService;

    @SuppressWarnings("unchecked")
    public void run() {

        outboxRepositoryFactory.getOutboxRepositories().forEach(outboxRepository -> {

            List<BaseEventEntity> notPublished = outboxRepository
                .findFirstNotPublishedSince(outboxPatternConfiguration.getMaxTimeWaitForSendInMils());
            log.info("Retrieve entities:" + notPublished.size() + " for repo:" + outboxRepository + " oldest than "
                    + outboxPatternConfiguration.getMaxTimeWaitForSendInMils() + " mils");
            batchProcess(notPublished);

        });
    }

    private void batchProcess(List<BaseEventEntity> packages) {
        log.info("Batch processing:{}", packages.size());
        Map<String, List<BaseEventEntity>> outboxEntityPerAggregateRoot = packages.stream()
            .collect(Collectors.groupingBy(BaseEventEntity::getAggregateRoot));

        parallelizableService.parallelize(
                ProcessOutboxEntityTask.builder()
                    .outboxEntities(outboxEntityPerAggregateRoot)
                    .outboxRepositoryFactory(outboxRepositoryFactory)
                    .outboxConfiguration(outboxPatternConfiguration)
                    .outboxEntityPostProcessor(outboxEntityPostProcessor)
                    .build(),
                outboxEntityPerAggregateRoot.keySet().stream().toList());

    }

}
