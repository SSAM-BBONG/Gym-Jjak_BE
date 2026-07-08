package com.ssambbong.gymjjak.part.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PartAlreadyExistsException extends ConflictException {

    public PartAlreadyExistsException() {
        super(PartErrorCode.PART_ALREADY_EXISTS, PartErrorCode.PART_ALREADY_EXISTS.getMessage());
    }
}
