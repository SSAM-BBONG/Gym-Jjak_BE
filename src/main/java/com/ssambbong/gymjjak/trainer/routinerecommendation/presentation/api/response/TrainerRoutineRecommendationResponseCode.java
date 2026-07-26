package com.ssambbong.gymjjak.trainer.routinerecommendation.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrainerRoutineRecommendationResponseCode implements ResponseCode {
    TRAINER_ROUTINE_RECOMMENDATION_CREATED("TRAINER_ROUTINE_RECOMMENDATION_200", "수강생 맞춤 루틴 추천이 완료되었습니다.");
    private final String code;
    private final String message;
}
