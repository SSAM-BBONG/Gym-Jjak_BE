package com.ssambbong.gymjjak.report.infrastructure.config;

import com.ssambbong.gymjjak.report.application.retention.ReportGroupRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ReportGroupRetentionProperties.class)
public class ReportRetentionConfig {
}
