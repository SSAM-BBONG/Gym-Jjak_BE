package com.ssambbong.gymjjak.trainer.trainerapplication.application.command;

import com.ssambbong.gymjjak.trainer.trainerprofile.application.command.ProfileImageUpdateAction;

import java.util.List;

public record UpdateTrainerApplicationCommand(
        // 수정할 트레이너 신청 ID
        Long trainerApplicationId,

        // 수정 요청한 사용자 ID
        Long requesterId,

        // 프로필 이미지 수정 방식
        ProfileImageUpdateAction profileImageAction,

        // 새 프로필 이미지 파일 메타 데이터
        UploadedFileMetadataCommand profileImageFile,

        // 변경할 자격증 목록
        List<String> qualifications,

        // 변경할 수상/대회경력 목록
        List<String> awardHistories,

        // 변경할 자기소개
        String introduction
) {
}
