package com.ssambbong.gymjjak.community.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {

    NOTICE_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_001", "공지글은 관리자만 작성할 수 있습니다."),
    COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY_404_001", "게시글을 찾을 수 없습니다."),
    COMMUNITY_POST_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_002", "본인의 게시글만 수정할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
