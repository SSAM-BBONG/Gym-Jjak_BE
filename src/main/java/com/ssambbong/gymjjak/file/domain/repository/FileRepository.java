package com.ssambbong.gymjjak.file.domain.repository;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;

import java.util.List;
import java.util.Optional;

public interface FileRepository {

    File save(File file);

    Optional<File> findById(Long fileId);

    List<File> findAllByIds(List<Long> fileIds);

    void deleteById(Long fileId);

    long countByFileType(FileType fileType);

    long count();

    long countActive();
}
