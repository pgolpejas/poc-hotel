package com.outbox.scheduler;

import java.io.Serial;

public class ParallelizableExecutionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2674279029181864829L;

    public ParallelizableExecutionException(final String message) {
        super(message);
    }

}
