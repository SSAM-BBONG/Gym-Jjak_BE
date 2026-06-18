package com.ssambbong.gymjjak.trainer.trainerprofile.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TrainerProfile {

    private final Long trainerProfileId;
    private final Long userId;
    private final Long applicationId;
    private final Long profileFileId;
    private final String trainerName;
    private final String introduction;
    private final BigDecimal averageRating;
    private final int reviewCount;
    private final TrainerProfileStatus status;

    @Builder(access = AccessLevel.PUBLIC)
    private TrainerProfile(
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

    public static TrainerProfile create(
            Long userId,
            Long applicationId,
            Long profileFileId,
            String trainerName,
            String introduction
    ) {
        return new TrainerProfile(
                null,
                userId,
                applicationId,
                profileFileId,
                trainerName,
                introduction,
                BigDecimal.ZERO,
                0,
                TrainerProfileStatus.ACTIVE
        );
    }

    public static TrainerProfile restore(
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
        return new TrainerProfile(
                trainerProfileId,
                userId,
                applicationId,
                profileFileId,
                trainerName,
                introduction,
                averageRating,
                reviewCount,
                status
        );
    }
}
