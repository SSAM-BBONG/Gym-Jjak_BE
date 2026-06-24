package com.ssambbong.gymjjak.file.infrastructure.persistence;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepository {

    private final SpringDataFileRepository springDataFileRepository;

    @Override
    public File save(File file) {
        FileJpaEntity entity = FileJpaEntity.fromDomain(file);
        FileJpaEntity savedEntity = springDataFileRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<File> findById(Long fileId) {
        return springDataFileRepository.findById(fileId)
                .map(FileJpaEntity::toDomain);
    }

    @Override
    public List<File> findAllByIds(List<Long> fileIds) {
        return springDataFileRepository.findAllById(fileIds).stream()
                .map(FileJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long fileId) {
        springDataFileRepository.deleteById(fileId);
    }

    @Override
    public long countByFileType(FileType fileType) {
        return springDataFileRepository.countByFileType(fileType);
    }

    @Override
    public long count() {
        return springDataFileRepository.count();
    }
}
