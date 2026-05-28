package com.ssambbong.gymjjak.category.domain.model;

import com.ssambbong.gymjjak.category.domain.exception.CategoryNameRequiredException;

public class Category {

    // 필드 선언
    private final Long id;
    private String name;

    // private 생성자. 외부에서 category 직접 생성 못하게 막음
    private Category(Long id, String name) {
        // name null 검증
        if (name == null || name.isBlank()) {
            throw new CategoryNameRequiredException();
        }
        this.id = id;
        this.name = name;
    }


    // id는 DB가 자동 부여하므로
    public static Category create(String name) {
        return new Category(null, name);
    }
    public static Category restore(Long id, String name) {
        return new Category(id, name);
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new CategoryNameRequiredException();
        }
        this.name = name;
    }

    // getter
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
