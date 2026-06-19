package com.ssambbong.gymjjak.file.application.usecase;

public interface FileUrlUseCase {
    String getUrl(Long fileId, Long requesterId, boolean isAdmin);
}
