package com.ssambbong.gymjjak.organization.organizationApplication.application.command;

public record UploadedFileMetadataCommand(
        String fileKey,
        String originalName,
        String contentType,
        Long fileSize
) {}
