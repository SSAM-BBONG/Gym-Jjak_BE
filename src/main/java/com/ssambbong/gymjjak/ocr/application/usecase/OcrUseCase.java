package com.ssambbong.gymjjak.ocr.application.usecase;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;

// ocr 사용자가 호출할 usecase
public interface OcrUseCase {

    OcrResult extractOcr(ExtractOcrCommand command);
}
