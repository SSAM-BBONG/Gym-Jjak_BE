package com.ssambbong.gymjjak.ocr.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record OcrResult(
        String matchedTemplateName, // ocr에 매칭된 템플릿 이름
        List<OcrExtractedField> fields // ocr에서 추출할 필드들
) {

    // 생성자: null 방어 + 불변성 보장
    public OcrResult {
        fields = fields == null ? List.of() : List.copyOf(fields);
    }

    // ocr에서 필드명으로 텍스트 값 찾기
    public Optional<String> findTextByName(String name) {
        return fields.stream()
                .filter(field -> Objects.equals(name, field.name()))
                .map(OcrExtractedField::inferText)
                .findFirst();
    }

    // ocr 안에 특정 필드 존재 여부 확인
    public boolean hasField(String name) {
        return fields.stream().anyMatch(
                field -> Objects.equals(name, field.name()));
    }
}
