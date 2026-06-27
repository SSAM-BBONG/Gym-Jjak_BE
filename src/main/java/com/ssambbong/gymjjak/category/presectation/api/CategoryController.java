package com.ssambbong.gymjjak.category.presectation.api;

import com.ssambbong.gymjjak.category.application.command.CreateCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.DeleteCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.UpdateCategoryCommand;
import com.ssambbong.gymjjak.category.application.usecase.CategoryCommandUseCase;
import com.ssambbong.gymjjak.category.application.usecase.CategoryQueryUseCase;
import com.ssambbong.gymjjak.category.presectation.api.request.CreateCategoryRequest;
import com.ssambbong.gymjjak.category.presectation.api.request.UpdateCategoryRequest;
import com.ssambbong.gymjjak.category.presectation.api.response.CategoryResponseCode;
import com.ssambbong.gymjjak.category.presectation.api.response.CreateCategoryResponse;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category", description = "카테고리 관리 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    // 유스케이스 의존
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final CategoryCommandUseCase categoryCommandUseCase;

    // 카테고리 목록 조회
    @Operation(summary = "카테고리 목록 조회", description = "카테고리 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<?>> getCategories() {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(CategoryResponseCode.CATEGORY_LIST_SUCCESS,
                        categoryQueryUseCase.handle())
        );
    }

    // 카테고리 등록
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 등록", description = "관리자가 카테고리를 등록한다.")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<?>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        Long categoryId = categoryCommandUseCase.handle(new CreateCategoryCommand(request.name()));
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(CategoryResponseCode.CATEGORY_CREATED, new CreateCategoryResponse(categoryId)));
    }

    // 카테고리 수정
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 수정", description = "관리자가 카테고리명을 수정한다.")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<GlobalApiResponse<?>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        categoryCommandUseCase.handle(new UpdateCategoryCommand(categoryId, request.name()));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(CategoryResponseCode.CATEGORY_UPDATED)
        );
    }

    // 카테고리 삭제
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제한다.")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<GlobalApiResponse<?>> deleteCategory(@PathVariable Long categoryId) {
        categoryCommandUseCase.handle(new DeleteCategoryCommand(categoryId));
        return ResponseEntity.ok(
                GlobalApiResponse.ok(CategoryResponseCode.CATEGORY_DELETED)
        );
    }


}
