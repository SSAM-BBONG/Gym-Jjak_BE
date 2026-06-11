package com.ssambbong.gymjjak.ocr.application.command;

/* Comment
*   MultipartFile을 그대로 service 계층으로 안넘기고
*   Controller 에서 bytes 형식으로 변환
* */
// TODO : 재원이가 이미지를 bytes 형태로 넘겨주면 변환 작업은 없어도 될듯?
public record ExtractOcrCommand(
        String originalFilename,
        String contentType,
        byte[] fileBytes
) {
}
