package com.ssambbong.gymjjak.chat.scheduler.infrastructure.config;

import com.ssambbong.gymjjak.chat.scheduler.application.retention.ChatRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChatRetentionProperties.class)
public class ChatRetentionConfig {
}
