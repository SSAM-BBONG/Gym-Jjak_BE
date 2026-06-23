package com.ssambbong.gymjjak.trainer.trainerprofile.application.command;

public record UpdateProfileImageFileCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {
}
