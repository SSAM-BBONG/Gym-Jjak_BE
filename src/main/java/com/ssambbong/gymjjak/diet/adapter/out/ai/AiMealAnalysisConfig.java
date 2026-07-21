package com.ssambbong.gymjjak.diet.adapter.out.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AiMealAnalysisProperties.class)
public class AiMealAnalysisConfig {
    @Bean
    public RestClient aiMealAnalysisRestClient(RestClient.Builder builder, AiMealAnalysisProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                // AI 서버에 연결하지 못할 때 MVC 요청 스레드가 장시간 묶이지 않도록 연결 제한 시간을 둔다.
                .connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMillis()))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        // 연결 후 AI 서버가 응답하지 않을 때도 지정된 시간 안에 동기 호출을 종료한다.
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.getResponseTimeoutSeconds()));

        return builder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // 모든 Java → FastAPI 호출에 동일한 서버 간 인증 키를 자동으로 포함한다.
                .defaultHeader("X-Internal-Api-Key", properties.getInternalApiKey())
                .build();
    }
}
