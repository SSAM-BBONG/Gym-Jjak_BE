package com.ssambbong.gymjjak.report.application.port;

// report 컨텍스트가 user에서 받아오는 최소 응답 모델

public record UserProfileView(
        Long userId,
        String username
) {
}
