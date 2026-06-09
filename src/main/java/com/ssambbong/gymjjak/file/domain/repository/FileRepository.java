package com.ssambbong.gymjjak.file.domain.repository;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.model.FileStatus;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;

import java.util.Optional;

/**
 * 파일 DB 저장소 포트 (인터페이스)
 * domain은 JPA가 뭔지 모름 - infrastructure에서 구현
 */
public interface FileRepository {

    // 파일 메타데이터 저장 → 저장된 File 반환
    File save(File file);

    // file_id로 파일 조회
    Optional<File> findById(Long fileId);

    // file_id로 파일 삭제 (soft delete) → 삭제된 행 수 반환 (0이면 존재하지 않음)
    boolean deleteById(Long fileId);

    // 타입별 파일 수 조회
    long countByFileType(FileType fileType);

    // 파일 전체 갯수 조회
    long count();

    // 파일 상태에 따른 갯수 조회
    long countByStatus(FileStatus fileStatus);
}
