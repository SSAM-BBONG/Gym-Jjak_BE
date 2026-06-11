package com.ssambbong.gymjjak.ocr.application.service;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.application.port.OcrClientPort;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrErrorCode;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/* Comment
*   Adapter는 Ocr Api 호출 로그가 찍힘
*   service는 공통 ocr 기능의 시작,성공을 로그찍음
*   비동기 실행: 호출 쓰레드를 블로킹하지 않고 ocrExecutor 쓰레드풀에서 실행됨
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService implements OcrUseCase {

    private final OcrClientPort ocrClientPort;

    @Async("ocrExecutor")
    @Override
    public CompletableFuture<OcrResult> extractOcr(ExtractOcrCommand command) {

        validateCommand(command);

        long startedAt = System.currentTimeMillis();

        log.info("event=ocr_extract_started contentType={}, fileSize={}, thread={}",
                command.contentType(),
                command.fileBytes().length,
                Thread.currentThread().getName()
        );

        OcrResult result = ocrClientPort.extractOcr(command);

        log.info(
                "event=ocr_extract_succeeded durationMs={}, templateName={}, fieldCount={}",
                System.currentTimeMillis() - startedAt,
                result.matchedTemplateName(),
                result.fields().size()
        );

        return CompletableFuture.completedFuture(result);
    }

    private void validateCommand(ExtractOcrCommand command) {
        if (command == null || command.fileBytes() == null || command.fileBytes().length == 0) {
            throw new OcrException(OcrErrorCode.OCR_FILE_READ_FAILED);
        }
    }
}
