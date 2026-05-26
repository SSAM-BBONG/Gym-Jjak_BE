package com.ssambbong.gymjjak.global.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // TODO : TraceId 만드는 TraceIdFilter 추가하기
    
    @Bean(name = "applicationTaskExecutor")
    public Executor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 개수
        executor.setCorePoolSize(5);
        // 최대 스레드 개수
        executor.setMaxPoolSize(10);
        // 작업 실패 시 대기할 큐 크기
        executor.setQueueCapacity(50);
        // 스레드 이름
        executor.setThreadNamePrefix("gymjjak-async-");
        // 작업 전 추가할 동작
        executor.setTaskDecorator(new MdcTaskDecorator());
        // 초기화
        executor.initialize();
        return executor;
    }

    // 기본 Async 실행 코드
    @Override
    public Executor getAsyncExecutor() {
        return applicationTaskExecutor();
    }

    // 비동기 스레드 중 예외 처리 핸들러
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new LoggingAsyncUncaughtExceptionHandler();
    }

    // 내부 클래스
    private static class MdcTaskDecorator implements TaskDecorator {

        // decorate 작업 사항
        // runnable을 감싼 새 Runnable 반환 후 클리어
        @Override
        public Runnable decorate(Runnable runnable) {
            // 현재 스레드의 MDC 값을 복사
            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            return () -> {
                try {
                    if (contextMap != null) {
                        // 복사해 둔 traceId 등을 현재 비동기 스레드에 추가
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
    }

    @Slf4j
    private static class LoggingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
        /* Comment
        *   - Throwable ex: 발생한 예외
        *   - Method method: 어떤 메서드에서 터졌는지
        *   - Object... params: 그 메서드에 전달된 인자들
        * */
        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            log.error(
                    "[AsyncUncaughtException] method={}, params={}",
                    method.getName(),
                    params,
                    ex
            );
        }
    }
}
