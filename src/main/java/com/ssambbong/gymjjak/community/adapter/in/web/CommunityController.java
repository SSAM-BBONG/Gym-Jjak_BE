package com.ssambbong.gymjjak.community.adapter.in.web;

import com.ssambbong.gymjjak.community.adapter.in.web.request.CreateCommunityCommentRequest;
import com.ssambbong.gymjjak.community.adapter.in.web.request.CreateCommunityPostRequest;
import com.ssambbong.gymjjak.community.adapter.in.web.request.UpdateCommunityCommentRequest;
import com.ssambbong.gymjjak.community.adapter.in.web.request.UpdateCommunityPostRequest;
import com.ssambbong.gymjjak.community.adapter.in.web.response.*;
import com.ssambbong.gymjjak.community.application.command.CreateCommunityPostLikeCommand;
import com.ssambbong.gymjjak.community.application.command.DeleteCommunityCommentCommand;
import com.ssambbong.gymjjak.community.application.command.DeleteCommunityPostCommand;
import com.ssambbong.gymjjak.community.application.command.DeleteCommunityPostLikeCommand;
import com.ssambbong.gymjjak.community.application.port.in.CommunityUseCase;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name = "Community", description = "커뮤니티에 관련된 기능")
public class CommunityController {

    private final CommunityUseCase communityUseCase;

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @PostMapping("/posts")
    @Operation(summary = "게시글 작성", description = "게시글을 작성한다")
    public ResponseEntity<GlobalApiResponse<CreateCommunityPostResponse>> createCommunityPost(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody CreateCommunityPostRequest request) {

        Long communityPostId =
                communityUseCase.createCommunityPost(
                        request.toCommand(
                                authUser.userId(),
                                authUser.role()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        GlobalApiResponse.created(
                                CommunityResponseCode.COMMUNITY_POST_CREATED,
                                CreateCommunityPostResponse.from(
                                        communityPostId
                                )
                        )
                );
    }

    @GetMapping("/posts")
    @Operation(summary = "게시글 목록 조회", description = "전체, 자유게시판, 공지 게시글 목록을 조회하는 요청이다.")
    public ResponseEntity<
            GlobalApiResponse<Page<CommunityPostListResponse>>> findCommunityPosts(
            @Parameter(description = "게시글 유형. 미입력 시 전체 게시글을 조회한다.", example = "FREE")
            @RequestParam(required = false) CommunityPostType type,
            @Parameter(description = "페이지 번호. 0부터 시작한다.", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size
        );

        Page<CommunityPostListResponse> response =
                communityUseCase.findCommunityPosts(
                                type,
                                pageable
                        )
                        .map(CommunityPostListResponse::from);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode.COMMUNITY_POST_LIST_FETCHED,
                                response
                        )
                );
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "커뮤니티 게시글의 상세 정보와 댓글 목록을 조회하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<CommunityPostDetailResponse>> findCommunityPostDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "댓글 Cursor ID. 최초 조회 시 입력하지 않는다.", example = "20")
            @RequestParam(required = false) Long commentCursorId,
            @Parameter(description = "댓글 조회 개수", example = "20")
            @RequestParam(defaultValue = "20") int commentSize
    ) {

        CommunityPostDetailResult result =
                communityUseCase.findCommunityPostDetail(
                        authUser.userId(),
                        postId,
                        commentCursorId,
                        commentSize
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode
                                        .COMMUNITY_POST_DETAIL_FETCHED,
                                CommunityPostDetailResponse.from(result)
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @PatchMapping("/posts/{postId}")
    @Operation(summary = "내 게시글 수정", description = "현재 로그인 사용자가 작성한 커뮤니티 게시글의 제목과 내용을 수정하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateCommunityPost(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "수정할 게시글 ID", example = "1")
            @PathVariable Long postId,
            @Valid @RequestBody UpdateCommunityPostRequest request) {

        communityUseCase.updateCommunityPost(
                request.toCommand(
                        authUser.userId(),
                        postId
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode.COMMUNITY_POST_UPDATED
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "내 게시글 삭제", description = "현재 로그인 사용자가 작성한 커뮤니티 게시글을 삭제하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteCommunityPost(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "삭제할 게시글 ID", example = "1")
            @PathVariable Long postId) {

        communityUseCase.deleteCommunityPost(
                new DeleteCommunityPostCommand(
                        authUser.userId(),
                        postId
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode
                                        .COMMUNITY_POST_DELETED
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 작성", description = "현재 로그인 사용자가 커뮤니티 게시글에 댓글을 작성하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<CreateCommunityCommentResponse>> createCommunityComment(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "댓글을 작성할 게시글 ID", example = "1")
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommunityCommentRequest request) {

        Long commentId =
                communityUseCase.createCommunityComment(
                        request.toCommand(
                                authUser.userId(),
                                postId
                        )
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        GlobalApiResponse.created(
                                CommunityResponseCode
                                        .COMMUNITY_COMMENT_CREATED,
                                CreateCommunityCommentResponse.from(
                                        commentId
                                )
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "내 댓글 수정", description = "현재 로그인 사용자가 작성한 커뮤니티 댓글의 내용을 수정하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> updateCommunityComment(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "수정할 댓글 ID", example = "1")
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommunityCommentRequest request) {

        communityUseCase.updateCommunityComment(
                request.toCommand(
                        authUser.userId(),
                        commentId
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode
                                        .COMMUNITY_COMMENT_UPDATED
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "내 댓글 삭제", description = "현재 로그인 사용자가 작성한 커뮤니티 댓글을 삭제하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteCommunityComment(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "삭제할 댓글 ID", example = "1")
            @PathVariable Long commentId) {

        communityUseCase.deleteCommunityComment(
                new DeleteCommunityCommentCommand(
                        authUser.userId(),
                        commentId
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode
                                        .COMMUNITY_COMMENT_DELETED
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @PostMapping("/posts/{postId}/likes")
    @Operation(summary = "게시글 좋아요 등록", description = "현재 로그인 사용자가 커뮤니티 게시글에 좋아요를 등록하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> createCommunityPostLike(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "좋아요를 등록할 게시글 ID", example = "1")
            @PathVariable Long postId) {

        communityUseCase.createCommunityPostLike(
                new CreateCommunityPostLikeCommand(
                        authUser.userId(),
                        postId
                )
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        GlobalApiResponse.created(
                                CommunityResponseCode
                                        .COMMUNITY_POST_LIKE_CREATED
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('USER', 'TRAINER', 'ADMIN', 'ORGANIZATION')")
    @DeleteMapping("/posts/{postId}/likes")
    @Operation(summary = "게시글 좋아요 취소", description = "현재 로그인 사용자가 커뮤니티 게시글에 등록한 좋아요를 취소하는 요청이다.")
    public ResponseEntity<GlobalApiResponse<Void>> deleteCommunityPostLike(
            @AuthenticationPrincipal AuthUser authUser,
            @Parameter(description = "좋아요를 취소할 게시글 ID", example = "1")
            @PathVariable Long postId) {

        communityUseCase.deleteCommunityPostLike(
                new DeleteCommunityPostLikeCommand(
                        authUser.userId(),
                        postId
                )
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        GlobalApiResponse.ok(
                                CommunityResponseCode
                                        .COMMUNITY_POST_LIKE_DELETED
                        )
                );
    }
}
