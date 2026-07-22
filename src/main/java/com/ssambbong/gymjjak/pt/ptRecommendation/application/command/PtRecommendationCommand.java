package com.ssambbong.gymjjak.pt.ptRecommendation.application.command;

import com.ssambbong.gymjjak.pt.ptCourse.domain.model.PartType;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.exception.PtRecommendationInvalidException;
import com.ssambbong.gymjjak.pt.ptRecommendation.domain.model.PainOnset;

import java.util.List;

public record PtRecommendationCommand(
        Long userId,
        List<PartType> targetParts,
        int distanceLevel,
        boolean hasPain,
        String painArea,
        PainOnset painOnset
) {
    public PtRecommendationCommand {
        if (hasPain && painOnset == null) {
            throw new PtRecommendationInvalidException("hasPain=true인 경우 painOnset은 필수입니다.");
        }
        if (!hasPain && (painArea != null || painOnset != null)) {
            throw new PtRecommendationInvalidException("hasPain=false인 경우 painArea/painOnset은 비워야 합니다.");
        }
    }
}
