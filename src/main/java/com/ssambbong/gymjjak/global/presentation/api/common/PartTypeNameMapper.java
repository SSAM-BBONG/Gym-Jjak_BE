package com.ssambbong.gymjjak.global.presentation.api.common;

import com.ssambbong.gymjjak.global.domain.common.exception.InvalidArgumentException;
import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

public final class PartTypeNameMapper {

    private PartTypeNameMapper() {
    }

    public static PartType fromKoreanName(String part) {
        if (part == null || part.isBlank()) {
            throw new InvalidArgumentException("운동 부위는 필수입니다.");
        }

        return switch (part.trim()) {
            case "가슴" -> PartType.CHEST;
            case "등" -> PartType.BACK;
            case "어깨" -> PartType.SHOULDER;
            case "팔" -> PartType.ARM;
            case "복근" -> PartType.ABS;
            case "코어" -> PartType.CORE;
            case "하체" -> PartType.LEG;
            case "둔근" -> PartType.GLUTE;
            case "전신" -> PartType.FULL_BODY;
            default -> throw new InvalidArgumentException("지원하지 않는 운동 부위입니다.");
        };
    }

    public static String toKoreanName(PartType part) {
        if (part == null) {
            return null;
        }

        return switch (part) {
            case CHEST -> "가슴";
            case BACK -> "등";
            case SHOULDER -> "어깨";
            case ARM -> "팔";
            case ABS -> "복근";
            case CORE -> "코어";
            case LEG -> "하체";
            case GLUTE -> "둔근";
            case FULL_BODY -> "전신";
        };
    }
}
