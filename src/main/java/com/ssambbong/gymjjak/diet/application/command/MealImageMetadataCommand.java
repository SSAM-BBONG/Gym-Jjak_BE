package com.ssambbong.gymjjak.diet.application.command;

public record MealImageMetadataCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {
}
