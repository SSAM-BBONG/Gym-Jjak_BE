package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.metrics;

import com.ssambbong.gymjjak.trainer.trainerapplication.application.metrics.TrainerApplicationTimed;
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
public class TrainerApplicationMetricAspect {

    private final MeterRegistry meterRegistry;
    private final TrainerApplicationMetric trainerApplicationMetric;

    @Around("@annotation(trainerApplicationTimed)")
    public Object recordApplicationDuration(
            ProceedingJoinPoint joinPoint,
            TrainerApplicationTimed trainerApplicationTimed // 호출 메서드에 붙은 annotation 객체
    ) throws Throwable {
        // 시간 측정 시작
        Timer.Sample sample = Timer.start(meterRegistry);
        String outcome = trainerApplicationMetric.success();

        try {
            return joinPoint.proceed();
        } catch (RuntimeException exception) {
            outcome = trainerApplicationMetric.failure();
            throw exception;
        } finally {
            // 성공 실패 모두, duration 기록
            trainerApplicationMetric.recordApplicationDuration(
                    sample,
                    trainerApplicationTimed.operation(),
                    outcome
            );
        }
    }
}
