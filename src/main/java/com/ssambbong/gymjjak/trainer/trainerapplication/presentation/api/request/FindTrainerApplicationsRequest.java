package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.request;

import com.ssambbong.gymjjak.trainer.trainerapplication.domain.model.TrainerApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record FindTrainerApplicationsRequest(

        @Schema(description = "조회할 신청 상태. 미전달 시 PENDING으로 조회합니다.", example = "PENDING")
        TrainerApplicationStatus status,

        @Schema(description = "검색어. 이메일(username), 이름(name), 닉네임(nickname)을 대상으로 검색합니다.", example = "test01@test.com")
        @Size(max = 100, message = "검색어는 최대 100자까지 입력할 수 있습니다.")
        String keyword,

        @Schema(description = "페이지 번호. 프론트에서는 page + 1로 표시하면 됩니다.", example = "0")
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        Integer page,

        @Schema(description = "페이지 크기", example = "10")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 100)
        Integer size
) {

    // 기본값 : 대기상태
    public TrainerApplicationStatus resolvedStatus() {
        return status == null ? TrainerApplicationStatus.PENDING : status;
    }

    public String normalizedKeyword() {
        return keyword == null || keyword.isBlank() ? null : keyword.trim();
    }

    public int resolvedPage() {
        return page == null ? 0 : page;
    }

    public int resolvedSize() {
        return size == null ? 20 : size;
    }
}
