package com.roze.notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppointmentEventConsumer {

    @KafkaListener(topics = "appointment-events", groupId = "notification-service-group")
    public void publishMessage(String event) {
        log.info("Consumer received event form Kafka server: {}", event);
    }
}
