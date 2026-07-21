package com.ssambbong.gymjjak.global.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.service")
public class AiServiceProperties {
    private String baseUrl = "http://localhost:8000";
    private String internalApiKey = "local-development-only";
    private int connectTimeoutMillis = 3000;
    // FastAPI 쪽 Gemini 자체 타임아웃(최대 60초)보다 여유 있게 잡아야 Java가 먼저 타임아웃나지 않는다.
    private int responseTimeoutSeconds = 90;
}
