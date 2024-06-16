package com.roze.auditing_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roze.auditing_service.dto.event.AuditEvent;
import com.roze.auditing_service.mapper.AuditMapper;
import com.roze.auditing_service.persistance.AuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class AuditEventConsumer {

    @Autowired
    private AuditRepository auditRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuditMapper auditMapper;

    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    public void publishMessage(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String recordValue = record.value();
        log.info("Consumer received event form Kafka server: {}", recordValue);

        AuditEvent auditEvent = objectMapper.readValue(recordValue, AuditEvent.class);
        auditRepository.save(auditMapper.auditEventToAuditEntity(auditEvent));
        log.info("Audit event saved to the database: {}", auditEvent);
    }
}
