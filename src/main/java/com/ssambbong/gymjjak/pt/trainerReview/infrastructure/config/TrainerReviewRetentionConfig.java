package com.ssambbong.gymjjak.pt.trainerReview.infrastructure.config;

import com.ssambbong.gymjjak.pt.trainerReview.application.retention.TrainerReviewRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TrainerReviewRetentionProperties를 Spring Bean으로 활성화
@Configuration
@EnableConfigurationProperties(TrainerReviewRetentionProperties.class)
public class TrainerReviewRetentionConfig {
}
