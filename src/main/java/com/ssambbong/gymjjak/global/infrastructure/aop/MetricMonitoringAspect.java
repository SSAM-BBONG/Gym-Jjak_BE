package com.ssambbong.gymjjak.global.infrastructure.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
            sample.stop(Timer.builder(monitored.name())
                    .description(monitored.description())
                    .tag("domain", monitored.domain())
                    .tag("action", normalize(monitored.action()))
                    .tag("outcome", outcome)
                    .tag("exception", exceptionName)
                    .register(meterRegistry));
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim().toLowerCase();
    }
}
