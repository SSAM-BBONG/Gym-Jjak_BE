package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportTargetType {
    PT_COURSE("PT"),
    TRAINER_REVIEW("강사평"),
    COMMENT("댓글"),
    POST("게시글"),
    FEEDBACK("피드백"),
    CHAT("채팅");

    private final String description;
}
