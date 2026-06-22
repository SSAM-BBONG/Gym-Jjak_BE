package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateTrainerProfileRequest(

        @Schema(
                description = """
                        프로필 이미지 수정 방식
                        KEEP: 기존 이미지 유지
                        REPLACE: 새 이미지로 교체
                        DELETE: 기존 이미지 삭제
                        """,
                example = "REPLACE"
        )
        @NotNull(message = "profileImageAction은 필수입니다.")
        ProfileImageUpdateAction profileImageAction,

        @Schema(
                description = """
                        새 프로필 파일 메타 데이터.
                        profileImageAction이 REPLACE인 경우에만 전달합니다.
                        """
        )
        @Valid
        TrainerProfileImageFileRequest profileImageFile,

        @Schema(
                description = """
                        추가 자격증 전체 목록.
                        null -> 기존 목록 유지,
                        빈 배열 -> 모든 경력 삭제
                        """,
                example = "[\"NSCA-CPT\", \"ACSM 인증 트레이너\"]"
        )
        @Size(
                max = 30,
                message = "추가 자격증은 최대 30개까지 입력할 수 있습니다."
        )
        List<
                @NotBlank(message = "추가 자격증 이름은 공백일 수 없습니다.")
                @Size(
                        max = 100,
                        message = "추가 자격증 이름은 최대 100자까지 입력할 수 있습니다."
                )
                String> additionalCertifications,

        @Schema(
                description = """
                        수상 및 대회 경력 전체 목록.
                        null이면 기존 목록을 유지하고,
                        빈 배열이면 모든 경력을 삭제합니다.
                        """,
                example = "[\"2025 피트니스 대회 우승\"]"
        )
        @Size(
                max = 100,
                message = "수상 및 대회 경력은 최대 100개까지 입력할 수 있습니다."
        )
        List<
                @NotBlank(message = "수상 및 대회 경력은 공백일 수 없습니다.")
                @Size(
                        max = 150,
                        message = "수상 및 대회 경력은 최대 150자까지 입력할 수 있습니다."
                )
                String> awardHistories,

        @Schema(
                description = """
                        트레이너 자기소개.
                        null이면 기존 값 유지,
                        빈 문자열이면 자기소개 삭제
                        """,
                example = "안녕하세요. 예쁜 몸 만들어드릴게요."
        )
        @Size(
                max = 1000,
                message = "자기소개는 최대 1000자까지 입력할 수 있습니다."
        )
        String introduction
) {
}
