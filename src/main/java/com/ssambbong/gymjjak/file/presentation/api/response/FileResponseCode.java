package com.ssambbong.gymjjak.file.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileResponseCode implements ResponseCode {
    FILE_PRESIGNED_URL_GENERATED("FILE_200_1", "Presigned URL 발급 성공"),
    FILE_REGISTERED("FILE_200_2", "파일 등록 성공"),
    FILE_PRESIGNED_URL_RETRIEVED("FILE_200_3", "파일 조회 URL 발급 성공");

    private final String code;
    private final String message;
}
