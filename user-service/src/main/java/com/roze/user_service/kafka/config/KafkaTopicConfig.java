package com.roze.user_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userDeletionTopic() {
        return TopicBuilder
                .name("user-deletion-events")
                .build();
    }

    @Bean
    public  NewTopic cacheEvictionTopic() {
        return TopicBuilder
                .name("cache-eviction-events")
                .build();
    }
}
