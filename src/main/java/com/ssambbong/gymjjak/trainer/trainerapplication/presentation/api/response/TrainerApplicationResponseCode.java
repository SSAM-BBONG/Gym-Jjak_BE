package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TrainerApplicationResponseCode implements ResponseCode {

    TRAINER_APPLICATION_CREATED(
            "TRAINER_APPLICATION_201_1",
            "트레이너 신청이 완료되었습니다."
    ),

    TRAINER_APPLICATION_UPDATED(
            "TRAINER_APPLICATION_201_2",
            "트레이너 신청서 수정이 완료되었습니다."
    ),

    TRAINER_APPLICATION_DETAIL_FOUND(
            "TRAINER_APPLICATION_200_2",
            "트레이너 신청서 상세 조회에 성공했습니다."
    ),

    TRAINER_APPLICATION_LIST_FOUND(
            "TRAINER_APPLICATION_200_3",
            "트레이너 신청 목록 조회에 성공했습니다."
    ),

    TRAINER_APPLICATION_REVIEW_DETAIL_FOUND(
            "TRAINER_APPLICATION_200_4",
            "트레이너 신청서 관리자 상세 조회에 성공했습니다."
    ),

    TRAINER_APPLICATION_APPROVED(
            "TRAINER_APPLICATION_201_5",
            "트레이너 신청 승인이 완료되었습니다."
    ),

    TRAINER_APPLICATION_REJECTED(
            "TRAINER_APPLICATION_201_6",
            "트레이너 신청 반려가 완료되었습니다."
    );

    private final String code;
    private final String message;
}
