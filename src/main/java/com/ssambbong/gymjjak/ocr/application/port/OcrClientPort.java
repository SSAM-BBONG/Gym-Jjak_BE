package com.ssambbong.gymjjak.ocr.application.port;

import com.ssambbong.gymjjak.ocr.application.command.ExtractOcrCommand;
import com.ssambbong.gymjjak.ocr.domain.OcrResult;

// Infrastructure 계층에서 구현할 OCR 클라이언트 포트
// 실제 사용자는 OcrUseCase를 통해 OCR 기능을 호출하세요.
public interface OcrClientPort {

    OcrResult extractOcr(ExtractOcrCommand command);
}
