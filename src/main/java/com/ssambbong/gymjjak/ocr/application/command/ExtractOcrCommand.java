package com.ssambbong.gymjjak.ocr.application.command;

/* Comment
 *  OCR 추출 요청에 필요한 파일 정보입니다.
 *  OCR 패키지는 파일이 어디에 저장되어 있는지 알 필요가 없습니다.
 *  Controller의 MultipartFile에서 변환된 bytes일 수도 있고,
 *  S3에서 읽어온 bytes일 수도 있습니다.
 *  -
 *  즉, OCR은 originalFilename, contentType, fileBytes만 받아서
 *  외부 OCR API 호출에 집중합니다.
* */
// TODO : 재원이가 이미지를 bytes 형태로 넘겨주면 변환 작업은 없어도 될듯?
public record ExtractOcrCommand(
        // 원본 파일명 예: certificate.png
        String originalFilename,
        // 파일 MIME 타. 예: image/png, image/jpeg, application/pdf
        String contentType,
        // OCR API에 전달할 실제 파일 바이트
        byte[] fileBytes
) {
}
