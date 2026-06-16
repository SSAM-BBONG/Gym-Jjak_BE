package com.ssambbong.gymjjak.file.presentation.api.response;

public record GeneratePresignedUrlResponse(
        String presignedUrl,
        String fileKey)
{}
