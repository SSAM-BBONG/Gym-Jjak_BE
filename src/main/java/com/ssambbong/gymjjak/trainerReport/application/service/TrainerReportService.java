package com.ssambbong.gymjjak.trainerReport.application.service;

import com.ssambbong.gymjjak.trainerReport.application.port.MarketAggregationPort;
import com.ssambbong.gymjjak.trainerReport.application.port.MarketAggregationPort.CourseEnrollmentStat;
import com.ssambbong.gymjjak.trainerReport.application.port.MyPtCourseQueryPort;
import com.ssambbong.gymjjak.trainerReport.application.port.MyPtCourseQueryPort.MyPtCourseInfo;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.MarketTrendsSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.MyPtCourseSnapshot;
import com.ssambbong.gymjjak.trainerReport.domain.model.PartLabels;
import com.ssambbong.gymjjak.trainerReport.domain.repository.TrainerReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 트레이너 리포트 생성 흐름을 조율한다. 의도적으로 @Transactional을 붙이지 않는다 —
 * AI 응답을 기다리는 몇 초 동안 DB 커넥션을 붙잡고 있으면 안 되기 때문이다.
 * 실제 저장(트랜잭션 필요)은 TrainerReportPersistenceService에 위임한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerReportService {

    private final MarketAggregationPort marketAggregationPort;
    private final MarketTrendsAggregator marketTrendsAggregator;
    private final MyPtCourseQueryPort myPtCourseQueryPort;
    private final TrainerReportAiPort trainerReportAiPort;
    private final TrainerReportRepository trainerReportRepository;
    private final TrainerReportPersistenceService trainerReportPersistenceService;

    // targetMonth는 그 달의 1일(예: 2026-06-01)이어야 한다 — 리포트가 다루는 달(예: 7/1 배치 실행 시
    // targetMonth=6월 1일)을 배치(스케줄러)가 결정해서 넘겨준다.
    public void generateReport(Long trainerProfileId, Long userId, LocalDate targetMonth) {
        // 배치 재실행 시 이미 생성된 리포트가 있으면 AI 재호출 없이 건너뛴다(멱등성).
        if (trainerReportRepository.findByTrainerProfileIdAndTargetMonth(trainerProfileId, targetMonth).isPresent()) {
            log.info("event=trainer_report_already_exists trainerProfileId={} targetMonth={}",
                    trainerProfileId, targetMonth);
            return;
        }

        List<CourseEnrollmentStat> thisMonthStats = marketAggregationPort.findMonthlyEnrollmentStats(
                targetMonth.atStartOfDay(), targetMonth.plusMonths(1).atStartOfDay());
        LocalDate lastMonth = targetMonth.minusMonths(1);
        List<CourseEnrollmentStat> lastMonthStats = marketAggregationPort.findMonthlyEnrollmentStats(
                lastMonth.atStartOfDay(), targetMonth.atStartOfDay());

        MarketTrendsSnapshot marketTrends = marketTrendsAggregator.aggregate(thisMonthStats, lastMonthStats);

        List<MyPtCourseSnapshot> myPtCourses = myPtCourseQueryPort
                .findVisibleCoursesByTrainerProfileId(trainerProfileId)
                .stream()
                .map(this::toSnapshot)
                .toList();

        String report = trainerReportAiPort.generateReport(trainerProfileId, marketTrends, myPtCourses);

        trainerReportPersistenceService.save(trainerProfileId, userId, targetMonth, report, marketTrends);
    }

    private MyPtCourseSnapshot toSnapshot(MyPtCourseInfo course) {
        return new MyPtCourseSnapshot(
                course.title(), course.price(), course.totalSessionCount(), PartLabels.toKorean(course.part()));
    }
}
