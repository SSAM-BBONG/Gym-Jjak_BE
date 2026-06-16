package com.ssambbong.gymjjak.user.application.retention;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserRetentionProperties.class)
public class UserRetentionConfig {
}
