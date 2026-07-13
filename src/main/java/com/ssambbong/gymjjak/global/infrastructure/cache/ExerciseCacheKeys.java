package com.ssambbong.gymjjak.global.infrastructure.cache;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;

import java.util.Locale;

public final class ExerciseCacheKeys {

    private static final String ALL_KEYWORD = "__all__";

    private ExerciseCacheKeys() {
    }

    public static String list(
            PartType part,
            String keyword
    ) {
        return "part:%s:keyword:%s".formatted(
                part.name(),
                normalizeKeyword(keyword)
        );
    }

    public static String snapshot(
            Long exerciseId,
            PartType part
    ) {
        return "exercise:%d:part:%s".formatted(
                exerciseId,
                part.name()
        );
    }

    private static String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return ALL_KEYWORD;
        }
        return keyword.trim().toLowerCase(Locale.ROOT);
    }
}
