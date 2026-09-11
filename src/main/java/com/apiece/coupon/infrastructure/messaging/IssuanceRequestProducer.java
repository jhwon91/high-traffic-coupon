package com.apiece.coupon.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IssuanceRequestProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public IssuanceRequestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(IssuanceRequested event) {
        kafkaTemplate.send(IssuanceTopics.REQUESTED, String.valueOf(event.getUserId()),event);
    }

}
