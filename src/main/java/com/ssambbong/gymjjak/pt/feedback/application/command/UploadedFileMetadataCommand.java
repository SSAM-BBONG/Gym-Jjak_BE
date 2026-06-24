package com.ssambbong.gymjjak.pt.feedback.application.command;

public record UploadedFileMetadataCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {
}
