package com.ssambbong.gymjjak.category.application.usecase;

import com.ssambbong.gymjjak.category.application.command.CreateCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.DeleteCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.UpdateCategoryCommand;

public interface CategoryCommandUseCase {

    // 카테고리 등록 -> 생성된 카테고리 id 반환
    Long handle(CreateCategoryCommand command);

    // 카테고리 수정
    void handle(UpdateCategoryCommand command);

    // 카테고리 삭제
    void handle(DeleteCategoryCommand command);
}
