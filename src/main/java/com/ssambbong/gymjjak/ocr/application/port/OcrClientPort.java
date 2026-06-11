package com.ssambbong.gymjjak.ocr.application.port;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;

// OCR 사용하려는 사람들 이 Port 호출하면 됩니다.
public interface OcrClientPort {

    OcrResult extractOcr(ExtractOcrCommand command);
}
