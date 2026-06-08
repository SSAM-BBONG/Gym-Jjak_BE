package com.ssambbong.gymjjak.file.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FileMetrics {

    private final Counter fileUploadSuccessCounter;

    public FileMetrics(MeterRegistry meterRegistry) {
        this.fileUploadSuccessCounter = Counter.builder("gymjjak.file.upload.success")
                .description("파일 업로드 성공 횟수")
                .register(meterRegistry);
    }

    public void recordUploadSuccess() {
        fileUploadSuccessCounter.increment();
    }

}
