package com.ssambbong.gymjjak.global.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * FastAPI AI 서버(Gym-Jjak-AI)를 호출하는 모든 도메인이 공유하는 RestClient.
 * baseUrl/인증 헤더/timeout처럼 도메인과 무관하게 동일한 연결 설정만 여기서 담당하고,
 * 요청/응답 DTO와 에러 매핑은 각 도메인의 Adapter가 개별적으로 가진다.
 */
@Configuration
@EnableConfigurationProperties(AiServiceProperties.class)
public class AiServiceConfig {
    @Bean
    public RestClient aiServiceRestClient(RestClient.Builder builder, AiServiceProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
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
