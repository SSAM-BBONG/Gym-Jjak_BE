package com.ssambbong.gymjjak.pt.ptRecommendation.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationInvalidException;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.model.PainOnset;

import java.util.List;

// 2차 온보딩(부위/거리/통증) 응답을 담는 커맨드. targetParts/distanceLevel은 1차 필터링에,
// hasPain 이하는 2차 AI 종합에 쓰인다.
public record PtRecommendationCommand(
        Long userId,
        List<PartType> targetParts,
        int distanceLevel,
        boolean hasPain,
        String painArea,
        PainOnset painOnset
) {
    // has_pain과 pain_area/pain_onset의 일관성은 여기서 즉시 검증한다
    public PtRecommendationCommand {
        if (hasPain && painOnset == null) {
            throw new PtRecommendationInvalidException("hasPain=true인 경우 painOnset은 필수입니다.");
        }
        if (!hasPain && (painArea != null || painOnset != null)) {
            throw new PtRecommendationInvalidException("hasPain=false인 경우 painArea/painOnset은 비워야 합니다.");
        }
    }
}
