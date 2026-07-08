package com.ssambbong.gymjjak.part.domain.model;

import com.ssambbong.gymjjak.part.domain.exception.PartNameRequiredException;

import java.time.LocalDateTime;

public class Part {

    private final Long id;
    private String name;
    private final LocalDateTime createdAt;

    private Part(Long id, String name, LocalDateTime createdAt) {
        if (name == null || name.isBlank()) {
            throw new PartNameRequiredException();
        }
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static Part create(String name) {
        return new Part(null, name, null);
    }

    public static Part restore(Long id, String name, LocalDateTime createdAt) {
        return new Part(id, name, createdAt);
    }

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new PartNameRequiredException();
        }
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
