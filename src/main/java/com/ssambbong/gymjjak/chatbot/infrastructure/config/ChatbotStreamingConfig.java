package com.ssambbong.gymjjak.chatbot.infrastructure.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

// 서버의 최대 스레드 풀 수 설정 클래스
@Configuration
public class ChatbotStreamingConfig {

    @Bean(name = "chatbotStreamingTaskExecutor")
    public ThreadPoolTaskExecutor chatbotStreamingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);    // 기본 스레디 풀
        executor.setMaxPoolSize(10);    // 대기열 큐 다 찾을 때 비상용 5개 생성
        executor.setQueueCapacity(50);  // 대기열 큐
        executor.setThreadNamePrefix("chatbot-stream-");
        executor.setTaskDecorator(mdcTaskDecorator());
        executor.initialize();
        return executor;
    }

    private TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }
}
