package com.ssambbong.gymjjak.report.infrastructure.metrics;

import com.ssambbong.gymjjak.report.application.metrics.ReportGroupTimed;
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
public class ReportGroupAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(reportGroupTimed)")
    public Object recordProcessingDuration(
            ProceedingJoinPoint joinPoint,
            ReportGroupTimed reportGroupTimed) throws Throwable {
        Timer.Sample  sample = Timer.start(meterRegistry);
        String outcome = "success";

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            outcome = "failure";
            throw throwable;
        } finally {
            sample.stop(Timer.builder("report.group.processing.duration")
                    .description("신고 그룹 처리 서비스 실행 시간")
                    .tag("action", normalizeAction(reportGroupTimed.action()))
                    .tag("outcome", outcome)
                    .register(meterRegistry));
        }
    }

    private String normalizeAction(String action) {
        if ("approve".equals(action)) {
            return "approve";
        }
        if ("reject".equals(action)) {
            return "reject";
        }
        return "unknown";
    }
}
