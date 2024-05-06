package com.outbox.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.outbox.configuration.OutboxConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParallelizableServiceImpl implements ParallelizableService {

  private final ExecutorService executorService;

  private final OutboxConfiguration outboxConfiguration;

  public ParallelizableServiceImpl(final OutboxConfiguration outboxConfiguration,
      final ExecutorService executorService) {
    this.outboxConfiguration = outboxConfiguration;
    this.executorService = executorService;
  }

  @Override
  public void parallelize(final ParallelizableTask task, List<String> partitions) {
    parallelize(task, partitions, executorService);
  }

  @Override
  public void parallelize(final ParallelizableTask task, final List<String> partitions,
      final ExecutorService executorService) {
    log.debug("ParallelizableServiceImpl::parallelize[multi-task] Start");
    
    final List<Future<Void>> futures = new ArrayList<>();

    partitions.forEach(partition -> futures.addAll(parallelizeWithoutWaiting(task, executorService, partition)));

    waitFutures(futures);

    log.debug("ParallelizableServiceImpl::parallelize[multi-task] End");
  }

  private List<Future<Void>> parallelizeWithoutWaiting(final ParallelizableTask task,
      final ExecutorService executorService, String partition) {
    final List<Callable<Void>> callables = new ArrayList<>();
    callables.add(() -> {
      task.execute(partition);

      return null;
    });
    try {
      return executorService.invokeAll(callables);
    } catch (final InterruptedException e) {
      throw handleInterruptedException(e);
    } catch (final Exception e) {
      log.error("ParallelizableServiceImpl::parallelizeWithoutWaiting exception");
      throw new ParallelizableExecutionException("ParallelizableService invokeAll fails.");
    }

  }

  private void waitFutures(final List<Future<Void>> futures) {
    for (final Future<?> future : futures) {
      try {
        log.debug("ParallelizableServiceImpl::waitFutures Start");

        future.get(outboxConfiguration.getMaxTimeForProcess(), TimeUnit.MILLISECONDS);

        log.debug("ParallelizableServiceImpl::waitFutures End");
      } catch (final InterruptedException e) {
        throw handleInterruptedException(e);
      } catch (final ParallelizableExecutionException e) {
        log.error("ParallelizableServiceImpl::waitFutures exception", e);
        // DON`T STOP THE PROCESS FOR THE OTHER AGGREGATES
      } catch (final Exception e) {
        log.error("ParallelizableServiceImpl::waitFutures exception", e);
        throw new ParallelizableExecutionException("ParallelizableService future fails");
      }
    }
  }

  private ParallelizableExecutionException handleInterruptedException(final InterruptedException ex) {
    final var errorMessage = "Thread interrupted";
    Thread.currentThread().interrupt();
    log.warn(errorMessage, ex);
    return new ParallelizableExecutionException(errorMessage);
  }

}
