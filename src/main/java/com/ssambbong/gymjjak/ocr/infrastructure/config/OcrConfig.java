package com.ssambbong.gymjjak.ocr.infrastructure.config;

import com.ssambbong.gymjjak.ocr.infrastructure.clova.ClovaOcrProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

import java.time.Duration;

// clovaProperties 를 등록
@Configuration
@EnableRetry
@EnableConfigurationProperties(ClovaOcrProperties.class)
public class OcrConfig {

    @Bean
    public RestClient clovaOcrRestClient(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // clova 서버 연결 자체는 3초 제한
        factory.setConnectTimeout(Duration.ofSeconds(3));
        // 응답 제한 시간 30초, 30초 보다 늦으면 요청 스레드 보호
        factory.setReadTimeout(Duration.ofSeconds(30));
        return builder
                .requestFactory(factory)
                .build();
    }
}
