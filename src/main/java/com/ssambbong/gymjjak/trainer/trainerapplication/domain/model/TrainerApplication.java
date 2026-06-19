package com.ssambbong.gymjjak.trainer.trainerapplication.domain.model;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;
import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.TrainerApplicationStatusConflictException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
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

    // 생성자는 private, Builder는 public
    // 이로 인해 다른 계층에서 도메인 계층 메서드 사용 가능
    @Builder(access = AccessLevel.PUBLIC)
    private TrainerApplication(
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
        this.trainerApplicationId = trainerApplicationId;
        this.userId = userId;
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

    // 트레이너 신청
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
                qualifications,
                awardHistories,
                introduction,
                TrainerApplicationStatus.PENDING,
                null,
                null,
                null
        );
    }

    // 트레이너 신청 수정
    public TrainerApplication updateApplication(
            Long profileFileId,
            List<String> qualifications,
            List<String> awardHistories,
            String introduction
    ) {
        return new TrainerApplication(
                this.trainerApplicationId,
                this.userId,
                profileFileId,
                this.certificateFileId, // 필수 자격증은 수정 x, 기존 값 유지
                qualifications,
                awardHistories,
                introduction,
                this.status,
                this.rejectReason,
                this.reviewedBy,
                this.reviewedAt
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
                qualifications,
                awardHistories,
                introduction,
                status,
                rejectReason,
                reviewedBy,
                reviewedAt
        );
    }

    public boolean isOwner(Long requesterId) {
        return this.userId.equals(requesterId);
    }

    public boolean isPending() {
        return this.status == TrainerApplicationStatus.PENDING;
    }

    public TrainerApplication approve(Long adminId, LocalDateTime reviewedAt) {

        if (!isPending()) {
            throw new TrainerApplicationStatusConflictException(
                    this.trainerApplicationId,
                    this.status
            );
        }

        if (adminId == null) {
            throw new InvalidTrainerApplicationException(
                    "승인 관리자 ID는 필수입니다."
            );
        }

        if (reviewedAt == null) {
            throw new InvalidTrainerApplicationException(
                    "승인 처리 시각은 필수입니다."
            );
        }

        return new TrainerApplication(
                this.trainerApplicationId,
                this.userId,
                this.profileFileId,
                this.certificateFileId,
                this.qualifications,
                this.awardHistories,
                this.introduction,
                TrainerApplicationStatus.APPROVED,
                null,
                adminId,
                reviewedAt
        );
    }

    public TrainerApplication reject(
            Long adminId,
            String rejectReason,
            LocalDateTime reviewedAt
    ) {
        if (!isPending()) {
            throw new TrainerApplicationStatusConflictException(
                    this.trainerApplicationId,
                    this.status
            );
        }

        if (adminId == null) {
            throw new InvalidTrainerApplicationException(
                    "반려 관리자 ID는 필수입니다."
            );
        }

        if (rejectReason == null || rejectReason.isBlank()) {
            throw new InvalidTrainerApplicationException(
                    "반려 사유는 필수입니다."
            );
        }

        if (reviewedAt == null) {
            throw new InvalidTrainerApplicationException(
                    "반려 처리 시각은 필수입니다."
            );
        }

        return new TrainerApplication(
                this.trainerApplicationId,
                this.userId,
                this.profileFileId,
                this.certificateFileId,
                this.qualifications,
                this.awardHistories,
                this.introduction,
                TrainerApplicationStatus.REJECTED,
                rejectReason,
                adminId,
                reviewedAt
        );
    }
}
