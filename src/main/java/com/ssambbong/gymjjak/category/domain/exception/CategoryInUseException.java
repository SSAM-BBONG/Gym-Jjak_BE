package com.ssambbong.gymjjak.category.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ConflictException;

public class CategoryInUseException extends ConflictException {

    public CategoryInUseException() {
        super(CategoryErrorCode.CATEGORY_IN_USE);
    }
}
