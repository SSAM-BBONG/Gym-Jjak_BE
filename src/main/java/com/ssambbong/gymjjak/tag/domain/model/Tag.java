package com.ssambbong.gymjjak.tag.domain.model;

import com.ssambbong.gymjjak.tag.domain.exception.TagNameRequiredException;

import java.time.LocalDateTime;

public class Tag {

    private final Long id;
    private String name;
    private final LocalDateTime createdAt;

    private Tag(Long id, String name, LocalDateTime createdAt) {
        if (name == null || name.isBlank()) {
            throw new TagNameRequiredException();
        }
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Tag create(String name) {
        return new Tag(null, name, null);
    }

    public static Tag restore(Long id, String name, LocalDateTime createdAt) {
        return new Tag(id, name, createdAt);
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new TagNameRequiredException();
        }
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
