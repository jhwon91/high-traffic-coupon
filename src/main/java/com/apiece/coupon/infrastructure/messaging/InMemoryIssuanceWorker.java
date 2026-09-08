package com.apiece.coupon.infrastructure.messaging;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryIssuanceWorker {
    private static final Logger log = LoggerFactory.getLogger(InMemoryIssuanceWorker.class);

    private final InMemoryIssuanceQueue queue;
    private final IssuanceWriter writer;

    public InMemoryIssuanceWorker(InMemoryIssuanceQueue queue, IssuanceWriter writer) {
        this.queue = queue;
        this.writer = writer;
    }

    private Thread workerThread;

    @PostConstruct
    public void start() {
        workerThread = new Thread(this::runLoop, "issuance-worker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    private void runLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            IssuanceRequested event;
            try {
                event = queue.poll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (event == null) {
                continue;
            }
            try {
                writer.write(event);
            } catch (Exception e) {
                log.error(
                        "Worker write 실패: couponId={}, userId={}",
                        event.getCouponId(),
                        event.getUserId(),
                        e
                );
            }
        }
        log.info("issuance-worker 종료");
    }

    @PreDestroy
    public void stop() {
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }
}
