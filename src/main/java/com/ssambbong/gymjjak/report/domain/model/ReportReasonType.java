package com.ssambbong.gymjjak.report.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReasonType {
    SPAM("도배"),
    ADVERTISEMENT("광고"),
    ABUSE("욕설"),
    SEXUAL_CONTENT("음란물"),
    FRAUD("사기"),
    PRIVACY_EXPOSURE("개인정보"),
    ETC("기타");

    private final String description;
}
