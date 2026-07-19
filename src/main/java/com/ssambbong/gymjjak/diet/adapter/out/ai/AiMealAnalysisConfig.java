package com.ssambbong.gymjjak.diet.adapter.out.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import io.netty.channel.ChannelOption;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AiMealAnalysisProperties.class)
public class AiMealAnalysisConfig {
    @Bean
    public WebClient aiMealAnalysisWebClient(WebClient.Builder builder, AiMealAnalysisProperties properties) {
        HttpClient httpClient = HttpClient.create()
                // AI 서버에 TCP 연결을 맺지 못한 경우 빠르게 실패시켜 요청 스레드가 무한 대기하지 않게 한다.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeoutMillis())
                // 연결 후 AI 서버가 전체 응답을 주지 않는 경우에도 정해진 시간 안에 요청을 종료한다.
                .responseTimeout(Duration.ofSeconds(properties.getResponseTimeoutSeconds()));

        return builder
                .baseUrl(properties.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // 모든 Java → FastAPI 호출에 동일한 서버 간 인증 키를 자동으로 포함한다.
                .defaultHeader("X-Internal-Api-Key", properties.getInternalApiKey())
                .build();
    }
}
