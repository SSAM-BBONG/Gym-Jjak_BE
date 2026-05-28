package com.ssambbong.gymjjak.report.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseTimeEntity;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupSanctionStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report_groups")
public class ReportGroupJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportGroupId;

    @Column(nullable = false, length = 20, unique = true)
    private String reportNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportTargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    private Long targetOwnerId;

    @Column(length = 255)
    private String snapshotTitle;

    @Lob
    private String snapshotContent;

    @Column(length = 500)
    private String snapshotFileUrl;

    @Column(nullable = false)
    private int totalReportCount;

    @Column(nullable = false)
    private int effectiveReportCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportGroupReviewStatus reviewStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportGroupSanctionStatus sanctionStatus;

    private Long processedBy;
}
