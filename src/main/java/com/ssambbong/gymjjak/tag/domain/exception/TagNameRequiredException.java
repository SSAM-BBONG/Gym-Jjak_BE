package com.ssambbong.gymjjak.tag.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class TagNameRequiredException extends BadRequestException {

    public TagNameRequiredException() {
        super(TagErrorCode.TAG_NAME_REQUIRED, TagErrorCode.TAG_NAME_REQUIRED.getMessage());
    }
}
