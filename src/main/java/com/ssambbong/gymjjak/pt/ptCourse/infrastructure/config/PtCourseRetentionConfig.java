package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.config;

import com.ssambbong.gymjjak.pt.ptCourse.application.retention.PtCourseRetentionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// PtCourseRetentionProperties를 Spring Bean으로 활성화
@Configuration
@EnableConfigurationProperties(PtCourseRetentionProperties.class)
public class PtCourseRetentionConfig {
}
