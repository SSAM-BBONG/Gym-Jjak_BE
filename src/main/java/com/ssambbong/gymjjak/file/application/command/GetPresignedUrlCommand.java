package com.ssambbong.gymjjak.file.application.command;

public record GetPresignedUrlCommand(
        Long fileId,
        Long requesterId,
        boolean isAdmin
) {
}
