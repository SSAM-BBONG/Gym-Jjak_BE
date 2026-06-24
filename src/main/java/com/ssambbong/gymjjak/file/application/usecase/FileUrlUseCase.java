package com.ssambbong.gymjjak.file.application.usecase;

import com.ssambbong.gymjjak.file.application.result.FileUrlResult;

import java.util.List;
import java.util.Map;

public interface FileUrlUseCase {
    // String에서 FileUrlResult로 변경 — url + originalName 함께 반환
    FileUrlResult getUrl(Long fileId, Long requesterId, boolean isAdmin);
    Map<Long, FileUrlResult> getUrls(List<Long> fileIds, Long requesterId, boolean isAdmin);
}
