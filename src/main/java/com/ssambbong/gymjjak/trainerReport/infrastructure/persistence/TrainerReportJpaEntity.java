package com.ssambbong.gymjjak.trainerReport.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trainer_reports")
public class TrainerReportJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_report_id")
    private Long id;

    @Column(name = "trainer_profile_id", nullable = false)
    private Long trainerProfileId;

    @Column(name = "target_month", nullable = false)
    private LocalDate targetMonth;

    @Column(name = "report", nullable = false, columnDefinition = "TEXT")
    private String report;

    @Column(name = "market_trends_snapshot", nullable = false, columnDefinition = "JSON")
    private String marketTrendsSnapshot;

    @Builder
    private TrainerReportJpaEntity(Long id, Long trainerProfileId, LocalDate targetMonth, String report,
                                   String marketTrendsSnapshot) {
        this.id = id;
        this.trainerProfileId = trainerProfileId;
        this.targetMonth = targetMonth;
        this.report = report;
        this.marketTrendsSnapshot = marketTrendsSnapshot;
    }
}
