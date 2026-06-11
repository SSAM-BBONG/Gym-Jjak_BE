package com.ssambbong.gymjjak.ocr.infrastructure.clova.dto;

import java.util.List;

public record ClovaOcrMessageRequest(
        String version,
        String requestId,
        long timestamp,
        List<ClovaOcrImageRequest> images
) {
    public static ClovaOcrMessageRequest of(String requestId, String format) {
        return new ClovaOcrMessageRequest(
                "V2",
                requestId,
                System.currentTimeMillis(),
                List.of(new ClovaOcrImageRequest(format, "ocr-image"))
        );
    }

    public record ClovaOcrImageRequest(
            String format,
            String name
    ) {
    }
}
