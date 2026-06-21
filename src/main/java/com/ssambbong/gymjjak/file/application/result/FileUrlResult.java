package com.ssambbong.gymjjak.file.application.result;

// URL만 반환하던 구조에서 원본 파일명(originalName)도 함께 반환하도록 도입
public record FileUrlResult(String url, String originalName) {
}
