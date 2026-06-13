package com.ssambbong.gymjjak.file.application.endpoint;

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

        return new FileSummary(fileRepository.count(), countByType);
    }

    public record FileSummary(
            long totalFileCount,
            Map<FileType, Long> countByType
    ) {}
}
