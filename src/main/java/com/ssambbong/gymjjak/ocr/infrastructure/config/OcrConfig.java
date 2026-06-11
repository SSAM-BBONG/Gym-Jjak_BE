package com.ssambbong.gymjjak.ocr.infrastructure.config;

import com.ssambbong.gymjjak.ocr.infrastructure.clova.ClovaOcrProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// clovaProperties 를 등록
@Configuration
@EnableConfigurationProperties(ClovaOcrProperties.class)
public class OcrConfig {

    @Bean
    public RestClient clovaOcrRestClient(RestClient.Builder builder) {
        return builder.build();
    }
}
