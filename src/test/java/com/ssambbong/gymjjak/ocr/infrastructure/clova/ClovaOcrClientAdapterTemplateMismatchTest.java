package com.ssambbong.gymjjak.ocr.infrastructure.clova;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.ocr.infrastructure.metrics.OcrMetric;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ClovaOcrClientAdapterTemplateMismatchTest {

    private static final String INVOKE_URL = "https://example.com/ocr";

    private MockRestServiceServer server;
    private ClovaOcrClientAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        adapter = new ClovaOcrClientAdapter(
                new ObjectMapper(),
                new ClovaOcrProperties(INVOKE_URL, "test-secret"),
                builder.build(),
                new OcrMetric(new SimpleMeterRegistry())
        );
    }

    @Test
    void templateNotFoundMessage_returnsEmptyOcrResult() {
        server.expect(requestTo(INVOKE_URL))
                .andRespond(withSuccess("""
                        {
                          "requestId": "gymjjak-test-001",
                          "images": [
                            {
                              "inferResult": "FAILURE",
                              "message": "NOT_FOUND: not found matched template"
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        OcrResult result = adapter.extractOcr(imageCommand());

        assertThat(result.matchedTemplateName()).isNull();
        assertThat(result.fields()).isEmpty();
        server.verify();
    }

    @Test
    void noRequestedValidationResult_returnsEmptyOcrResult() {
        server.expect(requestTo(INVOKE_URL))
                .andRespond(withSuccess("""
                        {
                          "requestId": "gymjjak-test-002",
                          "images": [
                            {
                              "inferResult": "FAILURE",
                              "message": "template validation failed",
                              "validationResult": {
                                "result": "NO_REQUESTED"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        OcrResult result = adapter.extractOcr(imageCommand());

        assertThat(result.matchedTemplateName()).isNull();
        assertThat(result.fields()).isEmpty();
        server.verify();
    }

    private ExtractOcrCommand imageCommand() {
        return new ExtractOcrCommand(
                "certificate.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1}
        );
    }
}
