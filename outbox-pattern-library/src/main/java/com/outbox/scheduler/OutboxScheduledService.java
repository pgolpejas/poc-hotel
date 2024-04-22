package com.outbox.scheduler;


import com.outbox.stream.OutboxPublisherTask;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

@AllArgsConstructor
public class OutboxScheduledService {


    private static final int FIXED_RATE_DEFAULT = 30000;

    private static final int FIXED_DELAY_DEFAULT = 1000;

    private final OutboxPublisherTask outboxPublisherTask;

    @Scheduled(fixedRateString = "${outbox.fixedRate:" + FIXED_RATE_DEFAULT + "}",
            initialDelayString = "${outbox.initialDelay:" + FIXED_DELAY_DEFAULT + "}")
    @SchedulerLock(name = "searchForOutboxEventPendingTask", lockAtMostFor = "PT30S")
    public void searchForOutboxEventPendingTask() {
        outboxPublisherTask.run();
    }

}
