package com.ssambbong.gymjjak.tag.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.tag.application.command.CreateTagCommand;
import com.ssambbong.gymjjak.tag.application.command.DeleteTagCommand;
import com.ssambbong.gymjjak.tag.application.command.UpdateTagCommand;
import com.ssambbong.gymjjak.tag.application.usecase.TagCommandUseCase;
import com.ssambbong.gymjjak.tag.application.usecase.TagQueryUseCase;
import com.ssambbong.gymjjak.tag.presentation.api.request.CreateTagRequest;
import com.ssambbong.gymjjak.tag.presentation.api.request.UpdateTagRequest;
import com.ssambbong.gymjjak.tag.presentation.api.response.CreateTagResponse;
import com.ssambbong.gymjjak.tag.presentation.api.response.TagResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tag", description = "태그 관리 API")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagQueryUseCase tagQueryUseCase;
    private final TagCommandUseCase tagCommandUseCase;

    @Operation(summary = "태그 목록 조회", description = "관리자/트레이너가 태그 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<?>> getTags() {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(TagResponseCode.TAG_LIST_SUCCESS, tagQueryUseCase.handle())
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "태그 등록", description = "관리자가 태그를 등록한다.")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<?>> createTag(@Valid @RequestBody CreateTagRequest request) {
        Long tagId = tagCommandUseCase.handle(new CreateTagCommand(request.name()));
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(TagResponseCode.TAG_CREATED, new CreateTagResponse(tagId)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "태그 수정", description = "관리자가 태그명을 수정한다.")
    @PatchMapping("/{tagId}")
    public ResponseEntity<GlobalApiResponse<?>> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody UpdateTagRequest request) {
        tagCommandUseCase.handle(new UpdateTagCommand(tagId, request.name()));
        return ResponseEntity.ok(GlobalApiResponse.ok(TagResponseCode.TAG_UPDATED));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "태그 삭제", description = "관리자가 태그를 삭제한다.")
    @DeleteMapping("/{tagId}")
    public ResponseEntity<GlobalApiResponse<?>> deleteTag(@PathVariable Long tagId) {
        tagCommandUseCase.handle(new DeleteTagCommand(tagId));
        return ResponseEntity.ok(GlobalApiResponse.ok(TagResponseCode.TAG_DELETED));
    }
}
