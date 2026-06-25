package com.ssambbong.gymjjak.tag.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class TagInUseException extends ConflictException {

    public TagInUseException() {
        super(TagErrorCode.TAG_IN_USE, TagErrorCode.TAG_IN_USE.getMessage());
    }
}
