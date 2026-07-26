package com.ssambbong.gymjjak.trainerReport.presentation.api;

import com.ssambbong.gymjjak.trainerReport.application.service.TrainerReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// TEMP: 로컬 수동 테스트 전용 컨트롤러. 인증 없음 — 절대 커밋하지 말 것.
// 실제 운영에서는 TrainerReportBatchScheduler가 트리거를 담당한다.
@Tag(name = "[TEMP] TrainerReport 수동 테스트", description = "로컬 검증 전용, 인증 없음, 커밋 금지")
@RestController
@RequestMapping("/api/dev/trainer-reports")
@RequiredArgsConstructor
public class TrainerReportManualTriggerController {

    private final TrainerReportService trainerReportService;

    @Operation(
            summary = "[TEMP] 트레이너 리포트 수동 생성",
            description = "배치 스케줄러를 기다리지 않고, 지정한 트레이너의 리포트 생성 흐름"
                    + "(시장 집계 → AI 호출 → trainer_reports 저장 → 알림 이벤트 발행)을 즉시 실행한다. "
                    + "targetMonth를 생략하면 지난달 1일을 기준으로 계산한다. 로컬 수동 검증 전용이며 커밋하지 않는다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 및 저장 완료",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "AI 호출 실패, 트레이너 프로필 없음 등",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/generate")
    public String generate(
            @Parameter(description = "테스트할 트레이너의 trainer_profile_id", example = "1")
            @RequestParam Long trainerProfileId,
            @Parameter(description = "알림 수신용 userId (해당 트레이너의 로그인 계정 userId)", example = "2")
            @RequestParam Long userId,
            @Parameter(description = "리포트가 다룰 달의 1일 (yyyy-MM-dd). 생략 시 지난달 1일로 자동 계산",
                    example = "2026-06-01")
            @RequestParam(required = false) String targetMonth
    ) {
        LocalDate month = targetMonth != null
                ? LocalDate.parse(targetMonth)
                : LocalDate.now().minusMonths(1).withDayOfMonth(1);

        trainerReportService.generateReport(trainerProfileId, userId, month);

        return "trainer_reports 저장 완료 (trainerProfileId=" + trainerProfileId + ", targetMonth=" + month + ")";
    }
}
