package com.ssambbong.gymjjak.organization.scheduler.infrastructure.config;

import com.ssambbong.gymjjak.organization.scheduler.application.retention.OrganizationRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrganizationRetentionProperties.class)
public class OrganizationRetentionConfig {
}
