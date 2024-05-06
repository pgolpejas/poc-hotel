package com.outbox.stream;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class EventPublisherResult implements Serializable {

    private String errorMessage;

    private boolean successful;

    private boolean retryable;

}
