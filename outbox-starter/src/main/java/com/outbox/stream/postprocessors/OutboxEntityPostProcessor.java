package com.outbox.stream.postprocessors;


import com.outbox.data.BaseEventEntity;
import com.outbox.data.OutboxRuntimeException;

import java.util.List;

public interface OutboxEntityPostProcessor {

    boolean process(List<BaseEventEntity> entities, boolean checkOlderEvents) throws OutboxRuntimeException;

}
