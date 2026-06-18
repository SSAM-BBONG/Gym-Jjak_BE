package com.ssambbong.gymjjak.chat.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PtCourseNotFoundException extends BadRequestException {

    public PtCourseNotFoundException() {
        super(ChatRoomErrorCode.INVALID_PT_COURSE, ChatRoomErrorCode.INVALID_PT_COURSE.getMessage());
    }
}
