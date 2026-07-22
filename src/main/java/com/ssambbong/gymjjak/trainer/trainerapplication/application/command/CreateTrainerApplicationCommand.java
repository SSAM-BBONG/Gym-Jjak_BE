package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception.InvalidTrainerApplicationException;

import java.util.List;

public record CreateTrainerApplicationCommand(

        // 트레이너 신청 요청한 사용자 ID
        Long applicantUserId,

        // 신청 대상 조직 ID
        // 조직 도메인에서 선택된 organizationId
        List<Long> organizationIds,

        // 프로필 이미지 file
        UploadedFileMetadataCommand profileImageFile,

        // 필수 자격증 file.
        UploadedFileMetadataCommand certificateFile,

        // 자격증명
        // 필수 자격증 여부는 OCR 결과로 검증, 나머지는 그냥 입력
        List<String> qualifications,

        // 대회경력
        List<String> awardHistories,

        // 자기소개
        String introduction
) {
    public CreateTrainerApplicationCommand {
        if (applicantUserId == null || applicantUserId <= 0) {
            throw new InvalidTrainerApplicationException(
                    "applicantUserId는 1 이상이어야 합니다."
            );
        }

        organizationIds = normalizeOrganizationIds(organizationIds);

        if (certificateFile == null) {
            throw new InvalidTrainerApplicationException(
                    "필수 자격증 파일은 필수입니다."
            );
        }

        qualifications = normalizeList(
                qualifications,
                "qualifications"
        );

        awardHistories = normalizeList(
                awardHistories,
                "awardHistories"
        );

        introduction = introduction == null
                ? null
                : introduction.trim();
    }

    private static List<String> normalizeList(
            List<String> values,
            String fieldName
    ) {
        if (values == null) {
            return List.of();
        }

        if (values.stream().anyMatch(
                value -> value == null || value.isBlank()
        )) {
            throw new InvalidTrainerApplicationException(
                    fieldName + "에는 null 또는 공백 값을 포함할 수 없습니다."
            );
        }

        return values.stream()
                .map(String::trim)
                .toList();
    }

    private static List<Long> normalizeOrganizationIds(
            List<Long> organizationIds
    ) {
        if (organizationIds == null || organizationIds.isEmpty()) {
            throw new InvalidTrainerApplicationException(
                    "신청 대상 조직은 하나 이상이어야 합니다."
            );
        }

        // null or 0 이하는 예외
        if (organizationIds.stream().anyMatch(
                organizationId -> organizationId == null || organizationId <= 0
        )) {
            throw new InvalidTrainerApplicationException(
                    "신청 대상 조직 ID는 1 이상이어야 합니다."
            );
        }

        if (organizationIds.size() != organizationIds.stream().distinct().count()) {
            throw new InvalidTrainerApplicationException(
                    "신청 대상 조직 ID는 중복될 수 없습니다."
            );
        }

        return List.copyOf(organizationIds);
    }
}
