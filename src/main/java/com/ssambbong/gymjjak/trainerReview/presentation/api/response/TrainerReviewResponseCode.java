package com.ssambbong.gymjjak.trainerReview.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainerReviewResponseCode implements ResponseCode {

    TRAINER_REVIEW_CREATED("REVIEW_201", "강사평이 등록되었습니다."),
    TRAINER_REVIEW_UPDATED("REVIEW_UPDATED", "강사평 수정 성공"),
    TRAINER_REVIEW_DELETED("REVIEW_DELETED", "강사평 삭제 성공"),
    TRAINER_REVIEW_FETCHED("TRAINER_REVIEW_FETCHED", "강사평 목록 조회 성공");

    private final String code;
    private final String message;
}
