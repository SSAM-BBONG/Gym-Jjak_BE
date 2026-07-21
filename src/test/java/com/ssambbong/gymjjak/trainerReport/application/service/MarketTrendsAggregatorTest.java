package com.ssambbong.gymjjak.trainerReport.application.service;

import com.ssambbong.gymjjak.trainerReport.application.port.MarketAggregationPort.CourseEnrollmentStat;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.BodyPartTrendSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.MarketTrendsSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.PriceDistributionSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.SessionCountDistributionSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketTrendsAggregatorTest {

    private final MarketTrendsAggregator aggregator = new MarketTrendsAggregator();

    @Test
    @DisplayName("수강생 수 기준으로 부위별 percentage를 계산한다")
    void aggregatesBodyPartsByEnrollmentCount() {
        List<CourseEnrollmentStat> thisMonth = List.of(
                new CourseEnrollmentStat("LEG", 200_000, 8, 70),
                new CourseEnrollmentStat("CORE", 200_000, 8, 30)
        );

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, List.of());

        BodyPartTrendSnapshot leg = findBodyPart(result, "다리");
        assertEquals(70.0, leg.percentage());
        assertNull(leg.percentageChangeFromLastMonth()); // 지난달 데이터 없으면 델타 null
    }

    @Test
    @DisplayName("지난달 데이터가 있으면 부위별 전월 대비 델타를 계산한다")
    void computesBodyPartDeltaWhenLastMonthExists() {
        List<CourseEnrollmentStat> thisMonth = List.of(new CourseEnrollmentStat("LEG", 200_000, 8, 80));
        List<CourseEnrollmentStat> lastMonth = List.of(
                new CourseEnrollmentStat("LEG", 200_000, 8, 60),
                new CourseEnrollmentStat("CORE", 200_000, 8, 40)
        );

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, lastMonth);

        BodyPartTrendSnapshot leg = findBodyPart(result, "다리");
        assertEquals(100.0, leg.percentage()); // 이번 달은 LEG밖에 없음
        assertEquals(40.0, leg.percentageChangeFromLastMonth()); // 100 - 60
    }

    @Test
    @DisplayName("가격 구간 경계값은 상한 쪽 구간에만 속한다 (맞닿은 구간 중복 방지)")
    void priceBucketBoundaryBelongsToUpperBucketOnly() {
        // 200,000원은 "15~20만원"의 상한이자 "20~25만원"의 하한
        List<CourseEnrollmentStat> thisMonth = List.of(new CourseEnrollmentStat("LEG", 200_000, 8, 10));

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, List.of());

        PriceDistributionSnapshot bucket15to20 = findPriceBucket(result, "15~20만원");
        PriceDistributionSnapshot bucket20to25 = findPriceBucket(result, "20~25만원");
        assertEquals(0.0, bucket15to20.percentage());
        assertEquals(100.0, bucket20to25.percentage());
    }

    @Test
    @DisplayName("최상위 가격 구간은 상한 없이 열려있다")
    void topPriceBucketHasNoUpperBound() {
        List<CourseEnrollmentStat> thisMonth = List.of(new CourseEnrollmentStat("LEG", 3_000_000, 8, 5));

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, List.of());

        PriceDistributionSnapshot topBucket = findPriceBucket(result, "50만원 이상");
        assertEquals(100.0, topBucket.percentage());
        assertNull(topBucket.maxPrice());
    }

    @Test
    @DisplayName("회차당가격은 price/sessionCount 기준으로 구간을 나누고, 델타는 항상 null이다")
    void aggregatesPricePerSessionWithoutDelta() {
        // 240,000원 / 8회 = 30,000원 → "3~3.5만원" 구간
        List<CourseEnrollmentStat> thisMonth = List.of(new CourseEnrollmentStat("LEG", 240_000, 8, 10));

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, List.of());

        PriceDistributionSnapshot bucket = result.pricePerSessionDistribution().stream()
                .filter(b -> b.priceRange().equals("3~3.5만원"))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, bucket.percentage());
        assertNull(bucket.percentageChangeFromLastMonth());
    }

    @Test
    @DisplayName("회차 수는 구간이 아니라 정확한 값 단위로 집계된다")
    void aggregatesSessionCountAsExactValues() {
        List<CourseEnrollmentStat> thisMonth = List.of(
                new CourseEnrollmentStat("LEG", 200_000, 8, 6),
                new CourseEnrollmentStat("LEG", 200_000, 12, 4)
        );

        MarketTrendsSnapshot result = aggregator.aggregate(thisMonth, List.of());

        SessionCountDistributionSnapshot eightSessions = result.sessionCountDistribution().stream()
                .filter(s -> s.sessionCount() == 8)
                .findFirst()
                .orElseThrow();
        assertEquals(60.0, eightSessions.percentage());
    }

    @Test
    @DisplayName("데이터가 없으면 모든 구간이 0%로 채워지고 예외가 발생하지 않는다")
    void handlesEmptyInputGracefully() {
        MarketTrendsSnapshot result = aggregator.aggregate(List.of(), List.of());

        assertTrue(result.popularBodyParts().isEmpty());
        assertFalse(result.priceDistribution().isEmpty()); // 구간 자체는 고정이라 항상 존재
        assertTrue(result.priceDistribution().stream().allMatch(b -> b.percentage() == 0.0));
    }

    private BodyPartTrendSnapshot findBodyPart(MarketTrendsSnapshot snapshot, String label) {
        return snapshot.popularBodyParts().stream()
                .filter(p -> p.bodyPart().equals(label))
                .findFirst()
                .orElseThrow(() -> new AssertionError(label + " 부위를 찾을 수 없음"));
    }

    private PriceDistributionSnapshot findPriceBucket(MarketTrendsSnapshot snapshot, String label) {
        return snapshot.priceDistribution().stream()
                .filter(p -> p.priceRange().equals(label))
                .findFirst()
                .orElseThrow(() -> new AssertionError(label + " 구간을 찾을 수 없음"));
    }
}
