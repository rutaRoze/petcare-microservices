package com.roze.appointment_service.kafka.producer;

import com.roze.appointment_service.dto.event.AppointmentEvent;
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
public class KafkaAppointmentEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEventToKafkaServer(String event) {

//        Message<String> message = MessageBuilder
//                .withPayload(event)
//                .setHeader(KafkaHeaders.TOPIC, "appointment-events")
//                .build();

        log.info("Sending event to Kafka server: {}", event);
        kafkaTemplate.send("appointment-events", event);
    }
}
