package com.ssambbong.gymjjak.pt.ptRecommendation.presentation.api.request;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptRecommendation.application.command.PtRecommendationCommand;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.model.PainOnset;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PtRecommendationRequest(

        @Schema(description = "원하는 운동 부위 (복수 선택 가능)")
        @NotEmpty(message = "부위를 1개 이상 선택해야 합니다.")
        List<PartType> targetParts,

        @Schema(description = "거리 슬라이더 단계 (1=가까운 동네 ~ 5=먼 동네)", example = "3")
        @Min(value = 1, message = "distanceLevel은 1 이상이어야 합니다.")
        @Max(value = 5, message = "distanceLevel은 5 이하여야 합니다.")
        int distanceLevel,

        @Schema(description = "통증/부상 여부")
        boolean hasPain,

        @Schema(description = "통증 부위 (무릎/허리/어깨/손목/발목 등, '그 외 부위' 선택 시 미입력)")
        String painArea,

        @Schema(description = "통증 발생 시기 (hasPain=true일 때 필수)")
        PainOnset painOnset
) {
    public PtRecommendationCommand toCommand(Long userId) {
        return new PtRecommendationCommand(userId, targetParts, distanceLevel, hasPain, painArea, painOnset);
    }
}
