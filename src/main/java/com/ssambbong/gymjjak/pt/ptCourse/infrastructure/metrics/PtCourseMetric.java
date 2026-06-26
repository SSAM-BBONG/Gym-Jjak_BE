package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.metrics;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PtCourseMetric {

    public PtCourseMetric(MeterRegistry meterRegistry, SpringDataPtCourseRepository ptCourseRepository) {
        Gauge.builder("gymjjak.pt.course.visible",
                        ptCourseRepository,
                        repo -> repo.countByStatus(PtCourseStatus.VISIBLE))
                .description("현재 공개 상태인 PT 코스 수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.pt.course.blocked",
                        ptCourseRepository,
                        repo -> repo.countByStatus(PtCourseStatus.BLOCKED))
                .description("현재 블라인드된 PT 코스 수 — 높으면 콘텐츠 품질 문제 신호")
                .register(meterRegistry);
    }
}
