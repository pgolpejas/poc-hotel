package com.outbox.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.outbox.configuration.OutboxConfiguration;
import com.outbox.configuration.OutboxRepositoryFactory;
import com.outbox.data.BaseEventEntity;
import com.outbox.stream.postprocessors.OutboxEntityPostProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class ProcessOutboxEntityTask implements ParallelizableTask {

  private final Map<String, List<BaseEventEntity>> outboxEntities;

  private final OutboxEntityPostProcessor outboxEntityPostProcessor;

  private final OutboxRepositoryFactory outboxRepositoryFactory;

  private final OutboxConfiguration outboxConfiguration;

  @Override
  public long count() {
    return outboxEntities.size();
  }

  @Override
  public void execute(String aggregate) throws ParallelizableExecutionException {
    final List<BaseEventEntity> subEntities = outboxEntities.get(aggregate);
    if (subEntities != null && !subEntities.isEmpty()) {
      log.info("Execute for subEntities:{} elements for aggregate:{}", subEntities.size(), aggregate);
      // Group by aggregateId. If there are more than one event for the same
      // aggregateId, we will process
      // them in order, otherwise can be parallelized.

      Map<String, List<BaseEventEntity>> outboxEntityPerAggregateId = subEntities.stream()
          .collect(Collectors.groupingBy(BaseEventEntity::getAggregateId));

      outboxEntityPerAggregateId.entrySet().forEach(entityList -> {
        List<BaseEventEntity> elements = entityList.getValue();
        elements.sort(Comparator.comparing(BaseEventEntity::getCreatedAt));
        List<List<BaseEventEntity>> smallerLists = Lists.partition(elements, 100);
        smallerLists.forEach(subList -> {
          try {
            log.info("Process sublist of {} for aggregate_id:{}", subList.size(), entityList.getKey());
            boolean send = outboxEntityPostProcessor.process(subList, false);
            if (!send) {
              String message = "There was an error sending package by scheduler for aggregated id:"
                  + entityList.getKey() + " stopping process for aggregate" + aggregate;
              log.error(message);
              return;
            } else {
              String message = "Sending package " + entityList.getKey() + " for aggregate" + aggregate;
              log.info(message);
            }
          } catch (Exception exception) {
            log.error("There was an error:" + exception, exception);
          }
        });

      });

    }
  }

}
