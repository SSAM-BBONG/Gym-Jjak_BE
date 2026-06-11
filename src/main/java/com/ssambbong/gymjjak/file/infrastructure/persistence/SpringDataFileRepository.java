package com.ssambbong.gymjjak.file.infrastructure.persistence;

import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFileRepository extends JpaRepository<FileJpaEntity, Long> {

    long countByFileType(FileType fileType);
}
