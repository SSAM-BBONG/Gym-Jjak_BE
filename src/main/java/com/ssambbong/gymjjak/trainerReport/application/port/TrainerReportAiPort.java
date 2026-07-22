package com.ssambbong.gymjjak.trainerReport.application.port;

import java.util.List;

public interface TrainerReportAiPort {
    // AI 서버의 리포트 생성 응답이 도착할 때까지 현재 요청 스레드에서 동기 대기한다.
    String generateReport(Long trainerId, MarketTrendsSnapshot marketTrends, List<MyPtCourseSnapshot> myPtCourses);

    record MarketTrendsSnapshot(
            List<BodyPartTrendSnapshot> popularBodyParts,
            List<PriceDistributionSnapshot> priceDistribution,
            List<PriceDistributionSnapshot> pricePerSessionDistribution,
            List<SessionCountDistributionSnapshot> sessionCountDistribution,
            List<LocationDistributionSnapshot> locationDistribution
    ) {
    }

    record BodyPartTrendSnapshot(String bodyPart, double percentage, Double percentageChangeFromLastMonth) {
    }

    // percentageChangeFromLastMonth는 총액 가격대(priceDistribution)에만 의미가 있다.
    // 회차당가격 분포(pricePerSessionDistribution)에는 이 필드를 null로 채운다.
    record PriceDistributionSnapshot(
            String priceRange,
            int minPrice,
            Integer maxPrice, // null이면 상한 없음(최고 구간)
            double percentage,
            Double percentageChangeFromLastMonth
    ) {
    }

    record SessionCountDistributionSnapshot(int sessionCount, double percentage) {
    }

    record LocationDistributionSnapshot(String region, double percentage) {
    }

    record MyPtCourseSnapshot(String name, int price, int sessionCount, String bodyPart) {
    }
}
