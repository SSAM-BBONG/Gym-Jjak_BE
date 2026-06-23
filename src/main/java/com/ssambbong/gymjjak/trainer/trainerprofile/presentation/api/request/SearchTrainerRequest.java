package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record SearchTrainerRequest(

        @Schema(
                description = """
                        트레이너 검색어.
                        name, username, nickname을 대상으로 검색.
                        null / 공백이면 전체 트레이너를 조회
                        """,
                example = "trainer01@test.com"
        )
        @Size(max = 100, message = "검색어는 최대 100자까지 입력할 수 있습니다.")
        String keyword,

        @Schema(description = "페이지 번호. 0부터 시작", example = "0")
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        Integer page,

        @Schema(description = "페이지당 조회 개수", example = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100, message = "페이지 크기는 최대 100까지 가능합니다.")
        Integer size
) {

    public String normalizeKeyword() {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    public int resolvePage() {
        return page == null ? 0 : page;
    }
    public int resolveSize() {
        return size == null ? 10 : size;
    }
}
