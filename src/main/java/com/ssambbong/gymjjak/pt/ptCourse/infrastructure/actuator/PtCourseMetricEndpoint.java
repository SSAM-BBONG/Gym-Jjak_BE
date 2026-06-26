package com.ssambbong.gymjjak.pt.ptCourse.infrastructure.actuator;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PtCourseStatus;
import com.ssambbong.gymjjak.pt.ptCourse.infrastructure.persistence.SpringDataPtCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Endpoint(id = "ptcourse")
@RequiredArgsConstructor
public class PtCourseMetricEndpoint {

    private final SpringDataPtCourseRepository ptCourseRepository;

    @ReadOperation
    public PtCourseSummary summary() {
        long visible = ptCourseRepository.countByStatus(PtCourseStatus.VISIBLE);
        long hidden = ptCourseRepository.countByStatus(PtCourseStatus.HIDDEN);
        long blocked = ptCourseRepository.countByStatus(PtCourseStatus.BLOCKED);
        long deleted = ptCourseRepository.countByStatus(PtCourseStatus.DELETED);

        List<NamedCountItem> categoryDistribution = ptCourseRepository.countGroupByCategoryName().stream()
                .map(row -> new NamedCountItem((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        List<NamedCountItem> tagDistribution = ptCourseRepository.countGroupByTagName().stream()
                .map(row -> new NamedCountItem((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        List<PriceRangeItem> priceDistribution = ptCourseRepository.countGroupByPriceRange().stream()
                .map(row -> new PriceRangeItem((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        return new PtCourseSummary(visible, hidden, blocked, deleted,
                categoryDistribution, tagDistribution, priceDistribution);
    }

    public record PtCourseSummary(
            long visibleCount,
            long hiddenCount,
            long blockedCount,
            long deletedCount,
            List<NamedCountItem> categoryDistribution,  // 카테고리별 PT 집중도
            List<NamedCountItem> tagDistribution,        // 태그별 PT 집중도
            List<PriceRangeItem> priceDistribution       // 가격대별 분포
    ) {}

    public record NamedCountItem(String name, long count) {}

    public record PriceRangeItem(String range, long count) {}
}
