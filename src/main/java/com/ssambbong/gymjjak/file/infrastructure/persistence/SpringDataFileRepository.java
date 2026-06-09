package com.ssambbong.gymjjak.file.infrastructure.persistence;

import com.ssambbong.gymjjak.file.domain.model.FileStatus;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SpringDataFileRepository extends JpaRepository<FileJpaEntity, Long> {

    @Modifying
    @Query("UPDATE FileJpaEntity f SET f.status = 'DELETED', f.deletedAt = :deletedAt WHERE f.fileId = :fileId AND f.deletedAt IS NULL")
    int softDeleteById(@Param("fileId") Long fileId, @Param("deletedAt") LocalDateTime deletedAt);

    long countByFileType(FileType fileType);

    long countByStatus(FileStatus status);
}
