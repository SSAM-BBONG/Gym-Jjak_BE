package com.ssambbong.gymjjak.ocr.application.usecase;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;

public interface OcrUseCase {

    OcrResult extractOcr(ExtractOcrCommand command);
}
