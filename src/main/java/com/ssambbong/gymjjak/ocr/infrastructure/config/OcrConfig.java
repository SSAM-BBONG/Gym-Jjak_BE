package com.ssambbong.gymjjak.ocr.infrastructure.config;

import com.ssambbong.gymjjak.ocr.infrastructure.clova.ClovaOcrProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

// OCR 비동기 처리, 타임아웃 설정을 포함한 설정 클래스
@Configuration
@EnableAsync
@EnableConfigurationProperties({ClovaOcrProperties.class, OcrProperties.class})
public class OcrConfig {

    @Bean
    public RestClient clovaOcrRestClient(RestClient.Builder builder, OcrProperties ocrProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(ocrProperties.connectTimeoutMs());
        requestFactory.setReadTimeout(ocrProperties.readTimeoutMs());

        return builder
                .requestFactory(requestFactory)
                .build();
    }

    // OCR 전용 비동기 쓰레드풀
    @Bean(name = "ocrExecutor")
    public Executor ocrExecutor(OcrProperties ocrProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(ocrProperties.threadPoolSize());
        executor.setMaxPoolSize(ocrProperties.threadPoolSize());
        executor.setQueueCapacity(ocrProperties.threadPoolQueueCapacity());
        executor.setThreadNamePrefix("ocr-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
