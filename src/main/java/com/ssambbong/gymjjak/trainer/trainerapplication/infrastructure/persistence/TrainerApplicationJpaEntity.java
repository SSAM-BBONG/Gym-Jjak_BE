package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplication;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(
        name = "trainer_applications",
        indexes = {
                @Index(
                        name = "idx_trainer_applications_user_status",
                        columnList = "user_id, status"
                ),
                @Index(
                        name = "idx_trainer_applications_status_created_id",
                        columnList = "status, created_at, trainer_application_id"
                ),
                @Index(
                        name = "idx_trainer_applications_organization_status_created_id",
                        columnList = "organization_id, status, created_at, trainer_application_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerApplicationJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_application_id")
    private Long trainerApplicationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "profile_file_id")
    private Long profileFileId;

    @Column(name = "certificate_file_id", nullable = false)
    private Long certificateFileId;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "qualifications", columnDefinition = "TEXT")
    private List<String> qualifications;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "award_histories", columnDefinition = "TEXT")
    private List<String> awardHistories;

    @Column(name = "introduction", nullable = false, columnDefinition = "TEXT")
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TrainerApplicationStatus status;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Builder
    private TrainerApplicationJpaEntity(
            Long trainerApplicationId,
            Long userId,
            Long organizationId,
            Long profileFileId,
            Long certificateFileId,
            List<String> qualifications,
            List<String> awardHistories,
            String introduction,
            TrainerApplicationStatus status,
            String rejectReason,
            Long reviewedBy,
            LocalDateTime reviewedAt
    ) {
        this.trainerApplicationId = trainerApplicationId;
        this.userId = userId;
        this.organizationId = organizationId;
        this.profileFileId = profileFileId;
        this.certificateFileId = certificateFileId;
        this.qualifications = qualifications == null ? List.of() : List.copyOf(qualifications);
        this.awardHistories = awardHistories == null ? List.of() : List.copyOf(awardHistories);
        this.introduction = introduction;
        this.status = status;
        this.rejectReason = rejectReason;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }
}
