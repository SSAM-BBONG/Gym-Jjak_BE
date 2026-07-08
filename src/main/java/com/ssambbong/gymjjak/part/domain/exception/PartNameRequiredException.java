package com.ssambbong.gymjjak.part.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class PartNameRequiredException extends BadRequestException {

    public PartNameRequiredException() {
        super(PartErrorCode.PART_NAME_REQUIRED, PartErrorCode.PART_NAME_REQUIRED.getMessage());
    }
}
