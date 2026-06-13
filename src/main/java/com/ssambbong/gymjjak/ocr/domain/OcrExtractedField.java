package com.ssambbong.gymjjak.ocr.domain;

public record OcrExtractedField(
        String name, // key값 이름
        String inferText, // value 값
        Double inferConfidence // ocr 인식 정확도
) {
}
