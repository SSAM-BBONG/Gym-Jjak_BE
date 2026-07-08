package com.ssambbong.gymjjak.part.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class PartInUseException extends ConflictException {

    public PartInUseException() {
        super(PartErrorCode.PART_IN_USE, PartErrorCode.PART_IN_USE.getMessage());
    }
}
