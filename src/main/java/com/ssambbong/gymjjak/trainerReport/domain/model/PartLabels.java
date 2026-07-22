package com.ssambbong.gymjjak.trainerReport.domain.model;

import java.util.Map;

// pt_courses.part ENUM 값을 리포트용 한글 라벨로 변환한다.
public final class PartLabels {

    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("CHEST", "가슴"),
            Map.entry("BACK", "등"),
            Map.entry("SHOULDER", "어깨"),
            Map.entry("ARM", "팔"),
            Map.entry("ABS", "복부"),
            Map.entry("CORE", "코어"),
            Map.entry("LEG", "다리"),
            Map.entry("GLUTE", "둔부"),
            Map.entry("FULL_BODY", "전신")
    );

    private PartLabels() {
    }

    public static String toKorean(String part) {
        return LABELS.getOrDefault(part, part);
    }
}
