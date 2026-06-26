package com.ssambbong.gymjjak.trainerReview.infrastructure.config;

import com.ssambbong.gymjjak.trainerReview.application.retention.TrainerReviewRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TrainerReviewRetentionProperties.class)
public class TrainerReviewRetentionConfig {
}
