package com.outbox.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;

public interface ParallelizableService {

    void parallelize(ParallelizableTask task, List<String> partitions);

    void parallelize(ParallelizableTask task, List<String> partitions, ExecutorService executorService);

}
