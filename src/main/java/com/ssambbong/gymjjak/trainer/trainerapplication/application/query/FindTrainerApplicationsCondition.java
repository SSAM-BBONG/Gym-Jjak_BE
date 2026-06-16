package com.ssambbong.gymjjak.trainer.trainerapplication.application.query;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;

// 검색 조건 객체
public record FindTrainerApplicationsCondition(
        TrainerApplicationStatus status,
        String keyword,
        int page,
        int size
) {

    private static final int MAX_SIZE = 100;

    public FindTrainerApplicationsCondition {
        if (status == null) {
            throw new IllegalArgumentException("상태값은 null 일 수 없습니다.");
        }

        if (page < 0) {
            throw new IllegalArgumentException("page는 0보다 작을 수 없습니다.");
        }

        if (size < 1) {
            throw new IllegalArgumentException("size는 1보다 작을 수 없습니다.");
        }

        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("사이즈는 " + MAX_SIZE + "보다 클 수 없습니다.");
        }

        keyword = normalizeKeyword(keyword);
    }

    private static String normalizeKeyword(String keyword) {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }
}
