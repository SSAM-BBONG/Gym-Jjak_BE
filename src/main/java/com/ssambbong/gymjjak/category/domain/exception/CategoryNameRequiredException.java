package com.ssambbong.gymjjak.category.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.BadRequestException;

public class CategoryNameRequiredException extends BadRequestException {

    public CategoryNameRequiredException() {
        super(CategoryErrorCode.CATEGORY_NAME_REQUIRED,
                CategoryErrorCode.CATEGORY_NAME_REQUIRED.getMessage());
    }
}
