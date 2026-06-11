package com.ssambbong.gymjjak.ocr.application.usecase;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;

import java.util.concurrent.CompletableFuture;

public interface OcrUseCase {

    CompletableFuture<OcrResult> extractOcr(ExtractOcrCommand command);
}
