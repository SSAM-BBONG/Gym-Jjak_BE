package com.ssambbong.gymjjak.trainer.trainerapplication.application.result;

// 프론트에서 보낸 파일 메타데이터를 File 도메인에 등록한 결과
public record RegisteredTrainerApplicationFiles(
        Long profileImageFileId,
        Long certificateFileId
) {
}
