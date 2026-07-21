package com.ssambbong.gymjjak.diet.adapter.out.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai.meal-analysis")
public class AiMealAnalysisProperties {
    private String baseUrl = "http://localhost:8000";
    private String analyzePath = "/api/v1/meals/analyze";
    private String internalApiKey = "local-development-only";
    private int connectTimeoutMillis = 3000;
    private int responseTimeoutSeconds = 30;
}
