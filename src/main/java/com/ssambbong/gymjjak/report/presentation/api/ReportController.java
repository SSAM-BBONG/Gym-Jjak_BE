package com.ssambbong.gymjjak.report.presentation.api;

import com.ssambbong.gymjjak.global.presentation.api.common.GlobalApiResponse;
import com.ssambbong.gymjjak.global.security.principal.AuthUser;
import com.ssambbong.gymjjak.report.application.usecase.CreateReportCommand;
import com.ssambbong.gymjjak.report.application.usecase.ReportCommandUseCase;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.presentation.api.request.CreateReportRequest;
import com.ssambbong.gymjjak.report.presentation.api.response.CreateReportResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "report", description = "신고 기능 REST-API")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReportController {

    private final ReportCommandUseCase reportCommandUseCase;

    @PostMapping
    public ResponseEntity<GlobalApiResponse<CreateReportResponse>> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal AuthUser authUser
            ) {
        Long reportId = reportCommandUseCase.createReport(
                new CreateReportCommand(
                        authUser.userId(),
                        request.targetId(),
                        request.targetType(),
                        request.reason(),
                        request.detail()
                ));

        return null;
    }

}
