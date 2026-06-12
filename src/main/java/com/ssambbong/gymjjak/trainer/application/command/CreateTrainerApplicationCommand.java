package com.ssambbong.gymjjak.trainer.application.command;

import java.util.List;

public record CreateTrainerApplicationCommand(

        // 트레이너 신청 요청한 사용자 ID
        Long applicantUserId,

        // 프로필 이미지 file
        Long profileImageFileId,

        // 필수 자격증 file.
        // 이 fileId로 File 도메인에서 S3 bytes를 읽고 OCR을 수행.
        Long certificateFileId,

        // 자격증명
        List<String> qualifications,

        // 대회경력
        List<String> competitionHistories,

        // 자기소개
        String introduction
) {
}
