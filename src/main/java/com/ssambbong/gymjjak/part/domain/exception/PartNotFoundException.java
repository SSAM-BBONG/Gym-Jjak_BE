package com.ssambbong.gymjjak.part.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class PartNotFoundException extends NotFoundException {

    public PartNotFoundException() {
        super(PartErrorCode.PART_NOT_FOUND, PartErrorCode.PART_NOT_FOUND.getMessage());
    }
}
