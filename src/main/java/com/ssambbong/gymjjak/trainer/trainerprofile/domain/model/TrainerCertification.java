package com.ssambbong.gymjjak.trainer.trainerprofile.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrainerCertification {

    private final Long trainerCertificationId;
    private final Long trainerProfileId;
    private final String name;
    private final Long fileId;
    private final TrainerCertificationType certificationType;

    @Builder(access = AccessLevel.PUBLIC)
    private TrainerCertification(
            Long trainerCertificationId,
            Long trainerProfileId,
            String name,
            Long fileId,
            TrainerCertificationType certificationType
    ) {
        this.trainerCertificationId = trainerCertificationId;
        this.trainerProfileId = trainerProfileId;
        this.name = name;
        this.fileId = fileId;
        this.certificationType = certificationType;
    }

    // 필수 자격증 status
    public static TrainerCertification required(
            Long trainerProfileId,
            String name,
            Long fileId
    ) {
        return new TrainerCertification(
                null,
                trainerProfileId,
                name,
                fileId,
                TrainerCertificationType.REQUIRED
        );
    }

    // 사용자 입력 자격증 status
    public static TrainerCertification additional(
            Long trainerProfileId,
            String name
    ) {
        return new TrainerCertification(
                null,
                trainerProfileId,
                name,
                null,
                TrainerCertificationType.ADDITIONAL
        );
    }

    public static TrainerCertification restore(
            Long trainerCertificationId,
            Long trainerProfileId,
            String name,
            Long fileId,
            TrainerCertificationType certificationType
    ) {
        return new TrainerCertification(
                trainerCertificationId,
                trainerProfileId,
                name,
                fileId,
                certificationType
        );
    }
}