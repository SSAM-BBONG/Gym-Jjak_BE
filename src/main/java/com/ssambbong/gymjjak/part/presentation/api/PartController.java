package com.ssambbong.gymjjak.part.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.part.application.command.CreatePartCommand;
import com.ssambbong.gymjjak.part.application.command.DeletePartCommand;
import com.ssambbong.gymjjak.part.application.command.UpdatePartCommand;
import com.ssambbong.gymjjak.part.application.usecase.PartCommandUseCase;
import com.ssambbong.gymjjak.part.application.usecase.PartQueryUseCase;
import com.ssambbong.gymjjak.part.presentation.api.request.CreatePartRequest;
import com.ssambbong.gymjjak.part.presentation.api.request.UpdatePartRequest;
import com.ssambbong.gymjjak.part.presentation.api.response.CreatePartResponse;
import com.ssambbong.gymjjak.part.presentation.api.response.PartResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Part", description = "부위 관리 API")
@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartQueryUseCase partQueryUseCase;
    private final PartCommandUseCase partCommandUseCase;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'TRAINER', 'USER')")
    @Operation(summary = "부위 목록 조회", description = "부위 목록을 조회한다.")
    @GetMapping
    public ResponseEntity<GlobalApiResponse<?>> getParts() {
        return ResponseEntity.ok(
                GlobalApiResponse.ok(PartResponseCode.PART_LIST_SUCCESS, partQueryUseCase.handle())
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "부위 등록", description = "관리자가 부위를 등록한다.")
    @PostMapping
    public ResponseEntity<GlobalApiResponse<?>> createPart(@Valid @RequestBody CreatePartRequest request) {
        Long partId = partCommandUseCase.handle(new CreatePartCommand(request.name()));
        return ResponseEntity.status(201)
                .body(GlobalApiResponse.created(PartResponseCode.PART_CREATED, new CreatePartResponse(partId)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "부위 수정", description = "관리자가 부위명을 수정한다.")
    @PatchMapping("/{partId}")
    public ResponseEntity<GlobalApiResponse<?>> updatePart(
            @PathVariable Long partId,
            @Valid @RequestBody UpdatePartRequest request) {
        partCommandUseCase.handle(new UpdatePartCommand(partId, request.name()));
        return ResponseEntity.ok(GlobalApiResponse.ok(PartResponseCode.PART_UPDATED));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "부위 삭제", description = "관리자가 부위를 삭제한다.")
    @DeleteMapping("/{partId}")
    public ResponseEntity<GlobalApiResponse<?>> deletePart(@PathVariable Long partId) {
        partCommandUseCase.handle(new DeletePartCommand(partId));
        return ResponseEntity.ok(GlobalApiResponse.ok(PartResponseCode.PART_DELETED));
    }
}
