package com.roze.user_service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserDeletionEvent(Long userProfileId) {
        sendEventToKafkaServer(userProfileId, "user-deletion-events");
    }

    public void sendCacheEvictionEvent(Long userProfileId) {
        sendEventToKafkaServer(userProfileId, "cache-eviction-events");
    }

    private void sendEventToKafkaServer(Long userProfileId, String topic) {
        Message<Long> message = MessageBuilder
                .withPayload(userProfileId)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        log.info("Sending event to Kafka server on topic {}: {}", topic, message);
        kafkaTemplate.send(message);
    }
}
