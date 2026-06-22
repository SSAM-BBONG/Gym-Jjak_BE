package com.ssambbong.gymjjak.trainer.trainerprofile.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrainerProfileResponseCode implements ResponseCode {

    MY_TRAINER_PROFILE_FOUND(
            "TRAINER_PROFILE_200_1",
            "내 트레이너 프로필 상세 조회에 성공했습니다."
    );

    private final String code;
    private final String message;
}
