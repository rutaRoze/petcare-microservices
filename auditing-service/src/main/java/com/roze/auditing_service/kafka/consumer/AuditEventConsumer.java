package com.roze.auditing_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuditEventConsumer {

    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    public void publishMessage(ConsumerRecord<String, String> record) {
        String recordValue = record.value();
        log.info("Consumer received event form Kafka server: {}", recordValue);
    }
}
