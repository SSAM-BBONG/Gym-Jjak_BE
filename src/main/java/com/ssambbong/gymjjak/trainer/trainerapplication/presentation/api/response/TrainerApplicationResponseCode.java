package com.ssambbong.gymjjak.trainer.trainerapplication.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainerApplicationResponseCode implements ResponseCode {

    TRAINER_APPLICATION_CREATED(
            "TRAINER_APPLICATION_201_1",
            "트레이너 신청이 완료되었습니다."
    ),

    TRAINER_APPLICATION_UPDATED(
            "TRAINER_APPLICATION_201_2",
            "트레이너 신청서 수정이 완료되었습니다.");

    private final String code;
    private final String message;
}
