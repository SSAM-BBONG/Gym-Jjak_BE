package com.ssambbong.gymjjak.global.infrastructure.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@Slf4j
public class PerformanceLogAspect {

    @Around("""
            execution(* com.ssambbong.gymjjak..service..get*(..)) ||
            execution(* com.ssambbong.gymjjak..service..find*(..))
            """)
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.nanoTime();
        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse("no-trace-id");

        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.nanoTime();

            long executionTimeMs = (endTime - startTime) / 1_000_000;
            double executionTimeSec = (endTime - startTime) / 1_000_000_000.0;

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            log.info("[PERFORMANCE] traceId={}, method={}.{}, executionTimeMs={}ms",
                    traceId,
                    signature.getDeclaringType().getSimpleName(),
                    signature.getMethod().getName(),
                    executionTimeMs
            );
        }
    }
}
