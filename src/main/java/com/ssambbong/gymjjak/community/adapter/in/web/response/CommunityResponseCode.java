package com.ssambbong.gymjjak.community.adapter.in.web.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityResponseCode implements ResponseCode {

    COMMUNITY_POST_CREATED("COMMUNITY_201_001", "게시글이 등록되었습니다."),
    COMMUNITY_POST_LIST_FETCHED("COMMUNITY_200_001", "게시글 목록을 조회했습니다."),
    COMMUNITY_POST_DETAIL_FETCHED("COMMUNITY_200_002", "게시글 상세를 조회했습니다."),
    COMMUNITY_POST_UPDATED("COMMUNITY_200_003", "게시글이 수정되었습니다."),
    COMMUNITY_POST_DELETED("COMMUNITY_200_004", "게시글이 삭제되었습니다."),
    COMMUNITY_COMMENT_CREATED("COMMUNITY_201_002", "댓글이 등록되었습니다."),
    COMMUNITY_COMMENT_UPDATED("COMMUNITY_200_005", "댓글이 수정되었습니다."),
    COMMUNITY_COMMENT_DELETED("COMMUNITY_200_006", "댓글이 삭제되었습니다."),
    COMMUNITY_POST_LIKE_CREATED("COMMUNITY_201_003", "게시글 좋아요가 등록되었습니다."),
    COMMUNITY_POST_LIKE_DELETED("COMMUNITY_200_007", "게시글 좋아요가 취소되었습니다.");

    private final String code;
    private final String message;
}
