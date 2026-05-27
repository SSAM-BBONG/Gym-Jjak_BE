package com.ssambbong.gymjjak.file.infrastructure.persistence;

import com.ssambbong.gymjjak.file.domain.model.File;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public void deleteById(Long fileId) {
        springDataFileRepository.findById(fileId)
                .ifPresent(entity -> {
                    entity.delete();
                    springDataFileRepository.save(entity);
                });
    }
}
