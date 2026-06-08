package com.ssambbong.gymjjak.global.infrastructure.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* Comment - 사용법
*   @Monitored(
        name = "report.processing.duration",
        domain = "report",
        action = "approve")
*   사용하고 싶은 joinPoint Service 위에 사용하면 됩니다.
* */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {
    String name();
    String description() default "";
    String domain() default "global";
    String action() default "unknown";
}
