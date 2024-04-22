package com.outbox.scheduler;

public interface ParallelizableTask {

    long count();
    
    void execute(String partition) throws ParallelizableExecutionException;

}
