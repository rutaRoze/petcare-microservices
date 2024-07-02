package com.roze.appointment_service.kafka.producer;

import com.roze.appointment_service.dto.event.AppointmentEvent;
import com.roze.appointment_service.dto.event.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventToKafkaServer(Object event) {
        String topic = determineTopic(event);

        Message<Object> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        log.info("Sending event to Kafka server: {}", message);
        kafkaTemplate.send(message);
    }

    private String determineTopic(Object event) {
        if (event instanceof AuditEvent) {
            return "audit-events";
        } else if (event instanceof AppointmentEvent) {
            return "appointment-events";
        } else {
            throw new IllegalArgumentException("Unknown event topic");
        }
    }
}
