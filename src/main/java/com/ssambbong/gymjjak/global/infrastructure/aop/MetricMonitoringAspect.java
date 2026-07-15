package com.ssambbong.gymjjak.global.infrastructure.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricMonitoringAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(monitored)")
    public Object recordDuration(
            ProceedingJoinPoint joinPoint,
            Monitored monitored
    ) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);

        String outcome = "success";
        String exceptionName = "none";

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            outcome = "failure";
            exceptionName = throwable.getClass().getName();
            throw throwable;
        } finally {
            recordDurationSafely(sample, monitored, outcome, exceptionName);
        }
    }

    // 메트릭 기록 실패가 비즈니스 요청 결과에 영향을 주지 않도록 처리
    private void recordDurationSafely(
            Timer.Sample sample,
            Monitored monitored,
            String outcome,
            String exceptionName
    ) {
        try {
            sample.stop(
                    Timer.builder(monitored.name())
                            .description(monitored.description())
                            .tag("domain", monitored.domain())
                            .tag("action", normalize(monitored.action()))
                            .tag("outcome", outcome)
                            .tag("exception", exceptionName)
                            .publishPercentiles(0.5, 0.90, 0.95, 0.99)
                            .register(meterRegistry)
            );
        } catch (RuntimeException exception) {
            log.warn(
                    "event=metric_record_failed metricName={}, domain={}, action={}, outcome={}",
                    monitored.name(),
                    monitored.domain(),
                    monitored.action(),
                    outcome,
                    exception
            );
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim().toLowerCase();
    }
}
