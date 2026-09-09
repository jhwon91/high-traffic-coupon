package com.apiece.coupon.infrastructure.messaging;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import static com.apiece.coupon.infrastructure.messaging.AsyncIssuanceConfig.ISSUANCE_TASK_EXECUTOR;

public class IssuanceEventHandler {
    private final IssuanceWriter writer;

    public IssuanceEventHandler(IssuanceWriter writer) {
        this.writer = writer;
    }

    @Async(ISSUANCE_TASK_EXECUTOR)
    @EventListener
    public void handle(IssuanceRequested event) {
        writer.write(event);
    }

}
