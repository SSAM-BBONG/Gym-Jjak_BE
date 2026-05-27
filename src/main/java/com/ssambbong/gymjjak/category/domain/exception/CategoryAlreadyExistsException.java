package com.ssambbong.gymjjak.category.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class CategoryAlreadyExistsException extends ConflictException {

    public CategoryAlreadyExistsException() {
        super(CategoryErrorCode.CATEGORY_ALREADY_EXISTS,
                CategoryErrorCode.CATEGORY_ALREADY_EXISTS.getMessage());
    }
}
