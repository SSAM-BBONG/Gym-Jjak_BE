package com.ssambbong.gymjjak.trainerReport.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainerReport {

    private final Long id;
    private final Long trainerProfileId;
    private final LocalDate targetMonth;
    private final String report;
    // AI 서버 호출 시점의 시장 데이터를 그대로 직렬화한 JSON 문자열.
    // 저장 후 내부 구조를 다시 조작할 일이 없어(상세 페이지 차트에 그대로 전달) 타입을 두지 않는다.
    private final String marketTrendsSnapshot;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private TrainerReport(Long id, Long trainerProfileId, LocalDate targetMonth, String report,
                          String marketTrendsSnapshot, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.trainerProfileId = trainerProfileId;
        this.targetMonth = targetMonth;
        this.report = report;
        this.marketTrendsSnapshot = marketTrendsSnapshot;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TrainerReport create(Long trainerProfileId, LocalDate targetMonth, String report,
                                       String marketTrendsSnapshot) {
        return new TrainerReport(null, trainerProfileId, targetMonth, report, marketTrendsSnapshot, null, null);
    }

    public static TrainerReport restore(Long id, Long trainerProfileId, LocalDate targetMonth, String report,
                                        String marketTrendsSnapshot, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        return new TrainerReport(id, trainerProfileId, targetMonth, report, marketTrendsSnapshot, createdAt,
                updatedAt);
    }

    public Long getId() { return id; }
    public Long getTrainerProfileId() { return trainerProfileId; }
    public LocalDate getTargetMonth() { return targetMonth; }
    public String getReport() { return report; }
    public String getMarketTrendsSnapshot() { return marketTrendsSnapshot; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
