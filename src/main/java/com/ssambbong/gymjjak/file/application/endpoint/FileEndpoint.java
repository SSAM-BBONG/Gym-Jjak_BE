package com.ssambbong.gymjjak.file.application.endpoint;

import com.ssambbong.gymjjak.file.domain.model.FileStatus;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import com.ssambbong.gymjjak.global.domain.common.model.FileType;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Endpoint(id = "file")
public class FileEndpoint {

    private final FileRepository fileRepository;

    public FileEndpoint(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @ReadOperation
    public FileSummary summary() {
        Map<FileType, Long> countByType = Arrays.stream(FileType.values())
                .collect(Collectors.toMap(
                        fileType -> fileType,
                        fileRepository::countByFileType
                ));

        return new FileSummary(
                fileRepository.count(),
                fileRepository.countByStatus(FileStatus.ACTIVE),
                fileRepository.countByStatus(FileStatus.DELETED),
                countByType
        );
    }

    public record FileSummary(
            long totalFileCount,
            long activeFileCount,
            long deletedFileCount,
            Map<FileType, Long> countByType
    ) {}
}
