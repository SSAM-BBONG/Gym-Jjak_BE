package com.ssambbong.gymjjak.file.application.usecase;

import java.util.List;
import java.util.Map;

public interface FileUrlUseCase {
    String getUrl(Long fileId, Long requesterId, boolean isAdmin);
    Map<Long, String> getUrls(List<Long> fileIds, Long requesterId, boolean isAdmin);
}
