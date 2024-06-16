package com.roze.appointment_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic appointmentTopic() {
        return TopicBuilder
                .name("appointment-events")
                .build();
    }

    @Bean
    public  NewTopic auditTopic() {
        return TopicBuilder
                .name("audit-events")
                .build();
    }
}
