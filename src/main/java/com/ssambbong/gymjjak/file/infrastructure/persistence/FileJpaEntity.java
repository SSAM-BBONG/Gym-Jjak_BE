package com.ssambbong.gymjjak.file.infrastructure.persistence;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import com.ssambbong.gymjjak.global.infrastructure.presentation.BaseCreatedDeletedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileJpaEntity extends BaseCreatedDeletedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "stored_name", nullable = false)
    private String storedName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Builder
    private FileJpaEntity(
            Long uploaderId,
            String originalName,
            String storedName,
            String fileUrl,
            String contentType,
            Long fileSize,
            FileType fileType
    ) {
        this.uploaderId = uploaderId;
        this.originalName = originalName;
        this.storedName = storedName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    // 도메인 모델 → JPA 엔티티 변환
    public static FileJpaEntity fromDomain(File file) {
        return FileJpaEntity.builder()
                .uploaderId(file.getUploaderId())
                .originalName(file.getOriginalName())
                .storedName(file.getStoredName())
                .fileUrl(file.getFileUrl())
                .contentType(file.getContentType())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .build();
    }

    // JPA 엔티티 → 도메인 모델 변환
    // DB 조회 시 validate() 호출을 피하기 위해 restore() 사용
    public File toDomain() {
        return File.restore(
                fileId,
                uploaderId,
                originalName,
                storedName,
                fileUrl,
                contentType,
                fileSize,
                fileType
        );
    }


}
