package com.ssambbong.gymjjak.ocr.application.service;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.application.port.OcrClientPort;
import com.ssambbong.gymjjak.ocr.application.usecase.OcrUseCase;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrErrorCode;
import com.ssambbong.gymjjak.ocr.domain.exception.OcrException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/* Comment
*   Adapter는 Ocr Api 호출 로그가 찍힘
*   service는 공통 ocr 기능의 시작,성공을 로그찍음
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService implements OcrUseCase {

    private final OcrClientPort ocrClientPort;

    @Override
    public OcrResult extractOcr(ExtractOcrCommand command) {

        validateCommand(command);

        long startedAt = System.currentTimeMillis();

        log.info("event=ocr_extract_started contentType={}, fileSize={}",
                command.contentType(),
                command.fileBytes().length
        );

        OcrResult result = ocrClientPort.extractOcr(command);

        log.info(
                "event=ocr_extract_succeeded durationMs={}, templateName={}, fieldCount={}",
                System.currentTimeMillis() - startedAt,
                result.matchedTemplateName(),
                result.fields().size()
        );

        return result;
    }

    private void validateCommand(ExtractOcrCommand command) {
        if (command == null || command.fileBytes() == null || command.fileBytes().length == 0) {
            throw new OcrException(OcrErrorCode.OCR_FILE_READ_FAILED);
        }
    }
}
