package com.ssambbong.gymjjak.community.adapter.in.web;

import com.ssambbong.gymjjak.community.adapter.in.web.request.CreateCommunityPostRequest;
import com.ssambbong.gymjjak.community.adapter.in.web.response.CommunityPostDetailResponse;
import com.ssambbong.gymjjak.community.adapter.in.web.response.CommunityPostListResponse;
import com.ssambbong.gymjjak.community.adapter.in.web.response.CommunityResponseCode;
import com.ssambbong.gymjjak.community.adapter.in.web.response.CreateCommunityPostResponse;
import com.ssambbong.gymjjak.community.application.port.in.CommunityUseCase;
import com.ssambbong.gymjjak.community.application.result.CommunityPostDetailResult;
import com.ssambbong.gymjjak.community.domain.type.CommunityPostType;
import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name = "Community", description = "커뮤니티에 관련된 기능")
public class CommunityController {

    private final CommunityUseCase communityUseCase;

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
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20")
            int size
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
    @Operation(
            summary = "게시글 상세 조회",
            description = "커뮤니티 게시글의 상세 정보와 댓글 목록을 조회하는 요청이다."
    )
    public ResponseEntity<
            GlobalApiResponse<CommunityPostDetailResponse>
            > findCommunityPostDetail(

            @AuthenticationPrincipal
            AuthUser authUser,

            @Parameter(
                    description = "게시글 ID",
                    example = "1"
            )
            @PathVariable
            Long postId
    ) {

        CommunityPostDetailResult result =
                communityUseCase
                        .findCommunityPostDetail(
                                authUser.userId(),
                                postId
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
}
