package com.ssambbong.gymjjak.tag.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TagNotFoundException extends NotFoundException {

    public TagNotFoundException() {
        super(TagErrorCode.TAG_NOT_FOUND, TagErrorCode.TAG_NOT_FOUND.getMessage());
    }
}
