package com.ssambbong.gymjjak.file.infrastructure.metrics;

import com.ssambbong.gymjjak.file.application.port.FileMetricsPort;
import com.ssambbong.gymjjak.file.domain.repository.FileRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FileMetrics implements FileMetricsPort {

    private final Counter presignedUrlGeneratedCounter;
    private final Counter fileRegisteredCounter;
    private final Counter fileDeletedCounter;

    public FileMetrics(MeterRegistry meterRegistry, FileRepository fileRepository) {
        this.presignedUrlGeneratedCounter = Counter.builder("gymjjak.file.presigned_url.generated")
                .description("Presigned URL 발급 횟수")
                .register(meterRegistry);
        this.fileRegisteredCounter = Counter.builder("gymjjak.file.registered")
                .description("파일 등록 횟수")
                .register(meterRegistry);
        this.fileDeletedCounter = Counter.builder("gymjjak.file.deleted")
                .description("파일 삭제 횟수")
                .register(meterRegistry);

        Gauge.builder("gymjjak.file.active.total", fileRepository, FileRepository::countActive)
                .description("현재 등록된 파일 수")
                .register(meterRegistry);
    }

    @Override
    public void recordPresignedUrlGenerated() {
        presignedUrlGeneratedCounter.increment();
    }

    @Override
    public void recordFileRegistered() {
        fileRegisteredCounter.increment();
    }

    @Override
    public void recordFileDeleted() {
        fileDeletedCounter.increment();
    }
}
