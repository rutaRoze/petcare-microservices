package com.roze.notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AppointmentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentEventConsumer.class);

    @KafkaListener(topics = "appointment-events", groupId = "notification-service-group")
    public void publishMessage(ConsumerRecord<String, String> record) {
        String recordValue = record.value();
        log.info("Consumer received event form Kafka server: {}", recordValue);
    }
}
