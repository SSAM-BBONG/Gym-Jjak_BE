package com.ssambbong.gymjjak.pt.feedback.infrastructure.config;

import com.ssambbong.gymjjak.pt.feedback.application.retention.FeedbackRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// FeedbackRetentionProperties를 Spring Bean으로 활성화
@Configuration
@EnableConfigurationProperties(FeedbackRetentionProperties.class)
public class FeedbackRetentionConfig {
}
