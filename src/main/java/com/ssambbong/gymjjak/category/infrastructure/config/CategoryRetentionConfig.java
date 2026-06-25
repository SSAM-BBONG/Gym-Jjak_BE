package com.ssambbong.gymjjak.category.infrastructure.config;

import com.ssambbong.gymjjak.category.application.retention.CategoryRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CategoryRetentionProperties.class)
public class CategoryRetentionConfig {
}
