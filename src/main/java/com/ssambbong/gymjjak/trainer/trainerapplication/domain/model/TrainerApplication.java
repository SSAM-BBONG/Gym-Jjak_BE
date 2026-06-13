package com.ssambbong.gymjjak.trainer.trainerapplication.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainerApplication {

    private final Long trainerApplicationId;

    // 신청자 id
    private final Long userId;

    // 프로필 이미지 fileId
    private final Long profileFileId;

    // 필수 자격증 fileId
    private final Long certificateFileId;

    // 자격증 목록
    private final List<String> qualifications;

    // 수상 경력 목록
    private final List<String> awardHistories;

    private final String introduction;
    private final TrainerApplicationStatus status;
    private final String rejectReason;
    private final Long reviewedBy;
    private final LocalDateTime reviewedAt;

    public static TrainerApplication create(
            Long userId,
            Long profileFileId,
            Long certificateFileId,
            List<String> qualifications,
            List<String> awardHistories,
            String introduction
    ) {
        return new TrainerApplication(
                null,
                userId,
                profileFileId,
                certificateFileId,
                qualifications == null ? List.of() : List.copyOf(qualifications),
                awardHistories == null ? List.of() : List.copyOf(awardHistories),
                introduction,
                TrainerApplicationStatus.PENDING,
                null,
                null,
                null
        );
    }

    public static TrainerApplication restore(
            Long trainerApplicationId,
            Long userId,
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
        return new TrainerApplication(
                trainerApplicationId,
                userId,
                profileFileId,
                certificateFileId,
                qualifications == null ? List.of() : List.copyOf(qualifications),
                awardHistories == null ? List.of() : List.copyOf(awardHistories),
                introduction,
                status,
                rejectReason,
                reviewedBy,
                reviewedAt
        );
    }
}
