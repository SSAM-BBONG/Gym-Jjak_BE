package com.ssambbong.gymjjak.tag.infrastructure.config;

import com.ssambbong.gymjjak.tag.application.retention.TagRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// TagRetentionProperties를 Spring Bean으로 활성화
@Configuration
@EnableConfigurationProperties(TagRetentionProperties.class)
public class TagRetentionConfig {
}
