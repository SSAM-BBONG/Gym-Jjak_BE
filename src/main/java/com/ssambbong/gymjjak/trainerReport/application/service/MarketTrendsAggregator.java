package com.ssambbong.gymjjak.trainerReport.application.service;

import com.ssambbong.gymjjak.trainerReport.application.port.MarketAggregationPort.CourseEnrollmentStat;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.BodyPartTrendSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.LocationDistributionSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.MarketTrendsSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.PriceDistributionSnapshot;
import com.ssambbong.gymjjak.trainerReport.application.port.TrainerReportAiPort.SessionCountDistributionSnapshot;
import com.ssambbong.gymjjak.trainerReport.domain.model.PartLabels;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * 원본 수강생 집계(CourseEnrollmentStat)를 FastAPI가 요구하는 MarketTrendsSnapshot 형태로 가공한다.
 * DB/외부 호출이 전혀 없는 순수 계산이라 별도 클래스로 분리했다 — comparison.py와 같은 이유.
 * 가격 구간 경계는 [min, max) 반개구간으로 판정한다(맞닿은 구간에 값이 중복 집계되는 것을 방지).
 */
@Component
public class MarketTrendsAggregator {

    private static final int PRICE_BUCKET_WIDTH = 50_000;
    private static final int PRICE_BUCKET_CEILING = 500_000;
    private static final int PRICE_PER_SESSION_BUCKET_WIDTH = 5_000;
    private static final int PRICE_PER_SESSION_BUCKET_CEILING = 50_000;

    public MarketTrendsSnapshot aggregate(List<CourseEnrollmentStat> thisMonth, List<CourseEnrollmentStat> lastMonth) {
        return new MarketTrendsSnapshot(
                aggregateBodyParts(thisMonth, lastMonth),
                aggregatePriceDistribution(thisMonth, lastMonth, CourseEnrollmentStat::price,
                        PRICE_BUCKET_WIDTH, PRICE_BUCKET_CEILING, true),
                aggregatePriceDistribution(thisMonth, List.of(), this::pricePerSession,
                        PRICE_PER_SESSION_BUCKET_WIDTH, PRICE_PER_SESSION_BUCKET_CEILING, false),
                aggregateSessionCounts(thisMonth),
                List.of() // 지역별 분포는 별도 작업으로 미룸(road_address 파싱 문제)
        );
    }

    private double pricePerSession(CourseEnrollmentStat stat) {
        return (double) stat.price() / stat.totalSessionCount();
    }

    // ── 인기 부위 ──

    private List<BodyPartTrendSnapshot> aggregateBodyParts(
            List<CourseEnrollmentStat> thisMonth, List<CourseEnrollmentStat> lastMonth) {
        Map<String, Long> thisMonthCounts = groupCountByPart(thisMonth);
        Map<String, Long> lastMonthCounts = groupCountByPart(lastMonth);
        long thisMonthTotal = sum(thisMonthCounts.values());
        long lastMonthTotal = sum(lastMonthCounts.values());

        return thisMonthCounts.entrySet().stream()
                .map(entry -> {
                    String part = entry.getKey();
                    double percentage = percentage(entry.getValue(), thisMonthTotal);
                    Double delta = lastMonthTotal == 0 ? null
                            : round1(percentage - percentage(lastMonthCounts.getOrDefault(part, 0L), lastMonthTotal));
                    return new BodyPartTrendSnapshot(PartLabels.toKorean(part), percentage, delta);
                })
                .sorted(Comparator.comparingDouble(BodyPartTrendSnapshot::percentage).reversed())
                .toList();
    }

    private Map<String, Long> groupCountByPart(List<CourseEnrollmentStat> stats) {
        return stats.stream().collect(Collectors.groupingBy(
                CourseEnrollmentStat::part, Collectors.summingLong(CourseEnrollmentStat::enrollmentCount)));
    }

    // ── 가격 / 회차당가격 구간 분포 ──

    private List<PriceDistributionSnapshot> aggregatePriceDistribution(
            List<CourseEnrollmentStat> thisMonth,
            List<CourseEnrollmentStat> lastMonth,
            ToDoubleFunction<CourseEnrollmentStat> valueExtractor,
            int bucketWidth,
            int bucketCeiling,
            boolean includeDelta
    ) {
        List<PriceBucket> buckets = buildBuckets(bucketWidth, bucketCeiling);

        Map<PriceBucket, Long> thisMonthCounts = groupCountByBucket(thisMonth, valueExtractor, buckets);
        long thisMonthTotal = sum(thisMonthCounts.values());

        Map<PriceBucket, Long> lastMonthCounts = includeDelta
                ? groupCountByBucket(lastMonth, valueExtractor, buckets)
                : Map.of();
        long lastMonthTotal = sum(lastMonthCounts.values());

        return buckets.stream()
                .map(bucket -> {
                    double percentage = percentage(thisMonthCounts.getOrDefault(bucket, 0L), thisMonthTotal);
                    Double delta = !includeDelta || lastMonthTotal == 0 ? null
                            : round1(percentage - percentage(lastMonthCounts.getOrDefault(bucket, 0L), lastMonthTotal));
                    return new PriceDistributionSnapshot(bucket.label(), bucket.min(), bucket.max(), percentage, delta);
                })
                .toList();
    }

    private Map<PriceBucket, Long> groupCountByBucket(
            List<CourseEnrollmentStat> stats, ToDoubleFunction<CourseEnrollmentStat> valueExtractor, List<PriceBucket> buckets) {
        Map<PriceBucket, Long> counts = new LinkedHashMap<>();
        for (CourseEnrollmentStat stat : stats) {
            double value = valueExtractor.applyAsDouble(stat);
            findBucket(buckets, value).ifPresent(bucket -> counts.merge(bucket, stat.enrollmentCount(), Long::sum));
        }
        return counts;
    }

    private Optional<PriceBucket> findBucket(List<PriceBucket> buckets, double value) {
        return buckets.stream().filter(b -> b.contains(value)).findFirst();
    }

    private List<PriceBucket> buildBuckets(int width, int ceiling) {
        List<PriceBucket> buckets = new ArrayList<>();
        for (int min = 0; min < ceiling; min += width) {
            int max = min + width;
            buckets.add(new PriceBucket(formatLabel(min, max), min, max));
        }
        buckets.add(new PriceBucket(formatLabel(ceiling, null), ceiling, null));
        return buckets;
    }

    private String formatLabel(int min, Integer max) {
        if (max == null) {
            return formatManwon(min) + "만원 이상";
        }
        return formatManwon(min) + "~" + formatManwon(max) + "만원";
    }

    // 5,000원처럼 만원 단위로 딱 안 떨어지는 값(2.5만원 등)의 소수점을 보존한다.
    // (min/10_000 정수 나눗셈으로 소수부가 잘려서 "2.5~3만원"이 "2~3만원"이 되던 버그 수정)
    private String formatManwon(int won) {
        double manwon = won / 10_000.0;
        if (manwon == Math.rint(manwon)) {
            return String.valueOf((long) manwon);
        }
        return String.valueOf(manwon);
    }

    // min 포함, max 미포함 — 맞닿은 두 구간에 경계값이 동시에 속하는 것을 방지한다.
    private record PriceBucket(String label, int min, Integer max) {
        boolean contains(double value) {
            if (value < min) return false;
            return max == null || value < max;
        }
    }

    // ── 회차 수 분포 (구간이 아니라 값 단위) ──

    private List<SessionCountDistributionSnapshot> aggregateSessionCounts(List<CourseEnrollmentStat> thisMonth) {
        Map<Integer, Long> counts = thisMonth.stream().collect(Collectors.groupingBy(
                CourseEnrollmentStat::totalSessionCount, Collectors.summingLong(CourseEnrollmentStat::enrollmentCount)));
        long total = sum(counts.values());

        return counts.entrySet().stream()
                .map(entry -> new SessionCountDistributionSnapshot(entry.getKey(), percentage(entry.getValue(), total)))
                .sorted(Comparator.comparingDouble(SessionCountDistributionSnapshot::percentage).reversed())
                .toList();
    }

    // ── 공통 유틸 ──

    private long sum(Collection<Long> values) {
        return values.stream().mapToLong(Long::longValue).sum();
    }

    private double percentage(long count, long total) {
        if (total == 0) return 0.0;
        return round1(count * 100.0 / total);
    }

    private double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
