package com.ssambbong.gymjjak.tag.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class TagAlreadyExistsException extends ConflictException {

    public TagAlreadyExistsException() {
        super(TagErrorCode.TAG_ALREADY_EXISTS, TagErrorCode.TAG_ALREADY_EXISTS.getMessage());
    }
}
