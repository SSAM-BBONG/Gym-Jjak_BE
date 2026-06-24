package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import com.ssambbong.gymjjak.file.presentation.api.request.UploadedFileMetadataRequest;
import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateTrainerApplicationRequest(

        @Schema(
                description = """
                        프로필 이미지 수정 방식.
                        KEEP: 기존 프로필 이미지 유지
                        REPLACE: 새 프로필 이미지로 교체
                        DELETE: 기존 프로필 이미지 삭제
                        """,
                example = "REPLACE"
        )
        @NotNull(message = "profileImageAction은 필수입니다.")
        ProfileImageUpdateAction profileImageAction,

        @Schema(
                description = """
                        새 프로필 이미지 파일 메타데이터.
                        profileImageAction이 REPLACE인 경우에만 전달합니다.
                        KEEP 또는 DELETE인 경우에는 null로 전달합니다.
                        """
        )
        @Valid
        UploadedFileMetadataRequest profileImageFile,

        @Schema(
                description = "수정할 자격증 목록입니다. 필수 자격증 파일 자체는 수정할 수 없습니다.",
                example = "[\"NSCA-CPT\", \"ACSM 인증 트레이너\"]"
        )
        @Size(max = 30, message = "자격증은 최대 30개까지 입력할 수 있습니다.")
        List<
                @NotBlank(message = "자격증 이름은 공백일 수 없습니다.")
                @Size(max = 100, message = "자격증 이름은 최대 100자까지 입력할 수 있습니다.")
                String> qualifications,


        @Schema(
                description = "수정할 수상/대회경력 목록입니다.",
                example = "[\"2023 피지크 대회 입상\"]"
        )
        @Size(max = 100, message = "수상/대회경력은 최대 100개까지 입력할 수 있습니다.")
        List<
                @NotBlank(message = "수상 및 대회 경력은 공백일 수 없습니다.")
                @Size(max = 150, message = "수상 및 대회 경력은 최대 150자까지 입력할 수 있습니다.")
                String> awardHistories,

        @Schema(description = "자기소개", example = "안녕하세요. 체형 교정 전문 트레이너입니다.")
        @Size(max = 1000, message = "자기소개는 최대 1000자까지 입력할 수 있습니다.")
        String introduction
) {
}
