package com.ssambbong.gymjjak.category.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND,
                CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }
}
