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
    COMMUNITY_POST_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_002", "본인의 게시글만 수정할 수 있습니다."),
    TITLE_REQUIRED(HttpStatus.NOT_FOUND, "COMMUNITY_404_002", "제목은 필수입니다."),
    CONTENT_REQUIRED(HttpStatus.NOT_FOUND, "COMMUNITY_404_003", "내용은 필수입니다."),
    COMMUNITY_POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_003", "본인의 게시글만 삭제할 수 있습니다."),
    COMMUNITY_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY_404_002", "댓글을 찾을 수 없습니다."),
    COMMUNITY_COMMENT_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_004", "본인의 댓글만 수정할 수 있습니다."),
    COMMUNITY_COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY_403_005", "본인의 댓글만 삭제할 수 있습니다."),
    COMMUNITY_POST_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMMUNITY_409_001", "이미 좋아요한 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
