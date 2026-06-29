package com.ssambbong.gymjjak.trainer.trainerprofile.infrastructure.persistence.entity;

import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedUpdatedEntity;
import com.ssambbong.gymjjak.trainer.trainerprofile.domain.model.TrainerProfileStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "trainer_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerProfileJpaEntity extends BaseCreatedUpdatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_profile_id")
    private Long trainerProfileId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "profile_file_id")
    private Long profileFileId;

    @Column(name = "trainer_name", nullable = false, length = 50)
    private String trainerName;

    @Column(name = "introduction", nullable = false, columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "average_rating", nullable = false)
    private BigDecimal averageRating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TrainerProfileStatus status;

    @Builder
    private TrainerProfileJpaEntity(
            Long trainerProfileId,
            Long userId,
            Long applicationId,
            Long profileFileId,
            String trainerName,
            String introduction,
            BigDecimal averageRating,
            int reviewCount,
            TrainerProfileStatus status
    ) {
        this.trainerProfileId = trainerProfileId;
        this.userId = userId;
        this.applicationId = applicationId;
        this.profileFileId = profileFileId;
        this.trainerName = trainerName;
        this.introduction = introduction;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.status = status;
    }
}
