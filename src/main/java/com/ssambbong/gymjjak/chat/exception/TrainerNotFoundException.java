package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerNotFoundException extends NotFoundException {

    public TrainerNotFoundException() {
        super(ChatRoomErrorCode.TRAINER_NOT_FOUND, ChatRoomErrorCode.TRAINER_NOT_FOUND.getMessage());
    }
}
