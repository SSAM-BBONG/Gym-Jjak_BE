package com.ssambbong.gymjjak.ocr.infrastructure.clova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrErrorCode;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrException;
import com.ssambbong.gymjjak.ocr.infrastructure.metrics.OcrMetric;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = ClovaOcrClientAdapterRetryTest.RetryTestConfiguration.class
)
class ClovaOcrClientAdapterRetryTest {

    @Autowired
    private ClovaOcrClientAdapter adapter;

    @Test
    void unsupportedFormat_propagatesOcrExceptionWithoutExhaustedRetryException() {
        ExtractOcrCommand command = new ExtractOcrCommand(
                "certificate.txt",
                "text/plain",
                new byte[]{1}
        );

        Throwable thrown = catchThrowable(() -> adapter.extractOcr(command));

        assertThat(thrown).isInstanceOf(OcrException.class);
        assertThat(((OcrException) thrown).getErrorCode())
                .isEqualTo(OcrErrorCode.OCR_UNSUPPORTED_FILE_FORMAT);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableRetry(proxyTargetClass = true)
    static class RetryTestConfiguration {

        @Bean
        ClovaOcrClientAdapter adapter() {
            return new ClovaOcrClientAdapter(
                    new ObjectMapper(),
                    new ClovaOcrProperties(
                            "https://example.com/ocr",
                            "test-secret"
                    ),
                    RestClient.create(),
                    new OcrMetric(new SimpleMeterRegistry())
            );
        }
    }
}
