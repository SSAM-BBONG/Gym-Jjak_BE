package com.ssambbong.gymjjak.category.application.command;

public record UpdateCategoryCommand(
        Long id,
        String name
) {
}
