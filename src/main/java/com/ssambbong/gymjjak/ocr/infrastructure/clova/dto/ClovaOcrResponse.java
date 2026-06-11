package com.ssambbong.gymjjak.ocr.infrastructure.clova.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaOcrResponse(
        String requestId, // clova api 추적 id
        List<ClovaOcrImageResponse> images
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClovaOcrImageResponse(
            String inferResult, // OCR 성공/실패 여부
            String message, // 실패 사유
            ClovaMatchedTemplate matchedTemplate, // 자격증/사업자등록증 템플릿 구분
            List<ClovaOcrFieldResponse> fields // 실제 추출 데이터
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClovaMatchedTemplate(
            Long id,
            String name // 요청 템플릿으로 인식됐는지 확인
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClovaOcrFieldResponse(
            String name, // OCR 템플릿 필드명
            String inferText, // 실제 추출 텍스트
            Double inferConfidence // 인식 신뢰도
    ) {
    }
}
