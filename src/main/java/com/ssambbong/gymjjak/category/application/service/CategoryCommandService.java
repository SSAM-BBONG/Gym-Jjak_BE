package com.ssambbong.gymjjak.category.application.service;

import com.ssambbong.gymjjak.category.application.command.CreateCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.DeleteCategoryCommand;
import com.ssambbong.gymjjak.category.application.command.UpdateCategoryCommand;
import com.ssambbong.gymjjak.category.application.usecase.CategoryCommandUseCase;
import com.ssambbong.gymjjak.category.domain.exception.CategoryAlreadyExistsException;
import com.ssambbong.gymjjak.category.domain.exception.CategoryInUseException;
import com.ssambbong.gymjjak.category.domain.exception.CategoryNotFoundException;
import com.ssambbong.gymjjak.category.domain.model.Category;
import com.ssambbong.gymjjak.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryCommandService implements CategoryCommandUseCase {

    private final CategoryRepository categoryRepository;

    // 카테고리 (이름) 등록
    @Override
    public Long handle(CreateCategoryCommand command) {
        // 중복 이름 확인
        if (categoryRepository.existsByName(command.name())) {
            throw new CategoryAlreadyExistsException();
        }
        // 도메인 객체 생성
        Category category = Category.create(command.name());
        // 저장 후 id 반환
        Category saved = categoryRepository.save(category);
        return saved.getId(); // 카테고리 id를 반환
    }

    // 카테고리 수정
    @Override
    public void handle(UpdateCategoryCommand command) {
        // 카테고리 존재 확인
        Category category = categoryRepository.findById(command.id())
                .orElseThrow(CategoryNotFoundException::new);
        // 중복 이름 확인 (자기 자신과 같은 경우 허용)
        if (!category.getName().equals(command.name()) &&
                categoryRepository.existsByName(command.name())) {
            throw new CategoryAlreadyExistsException();
        }
        // 이름 변경 후 저장
        category.changeName(command.name());
        categoryRepository.save(category);
    }

    // 카테고리 삭제 '
    @Override
    public void handle(DeleteCategoryCommand command) {
        // 카테고리 존재 확인
        categoryRepository.findById(command.id())
                .orElseThrow(CategoryNotFoundException::new);
        // PT 강습에서 사용 중이면 삭제 불가 (count 체크)
        if (categoryRepository.countPtCoursesByCategoryId(command.id()) > 0) {
            throw new CategoryInUseException();
        }
        // 삭제 — count 이후 동시 참조 발생 시 FK 제약 위반을 CategoryInUseException으로 변환
        try {
            categoryRepository.deleteById(command.id());
        } catch (DataIntegrityViolationException e) {
            throw new CategoryInUseException();
        }
    }
}
