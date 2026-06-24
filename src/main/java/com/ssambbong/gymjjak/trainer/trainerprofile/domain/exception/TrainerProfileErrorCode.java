package com.ssambbong.gymjjak.trainer.trainerprofile.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainerProfileErrorCode implements ErrorCode {

    TRAINER_PROFILE_INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "TRAINER_PROFILE_400_1",
            "트레이너 프로필 수정 요청값이 유효하지 않습니다."
    ),

    TRAINER_PROFILE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TRAINER_PROFILE_404_1",
            "트레이너 프로필을 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
