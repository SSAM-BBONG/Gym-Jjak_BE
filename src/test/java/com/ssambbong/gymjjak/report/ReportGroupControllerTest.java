package com.ssambbong.gymjjak.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.global.security.handler.CustomAccessDeniedHandler;
import com.ssambbong.gymjjak.global.security.handler.CustomAuthenticationEntryPoint;
import com.ssambbong.gymjjak.global.security.jwt.JwtAuthenticationFilter;
import com.ssambbong.gymjjak.report.application.query.*;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupQueryUseCase;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.*;
import com.ssambbong.gymjjak.report.presentation.api.ReportGroupController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ReportGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportGroupControllerTest {

    @MockitoBean
    private ReportGroupCommandUseCase reportGroupCommandUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportGroupQueryUseCase reportGroupQueryUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    AdminReportListItem item = new AdminReportListItem(
            999L,
            "RPT-001",
            ReportTargetType.PT_COURSE,
            10L,
            "욕설이 난무하는 PT",
            "트레이너 현자",
            LocalDateTime.of(2026, 5,28,17,10),
            3,
            ReportGroupReviewStatus.PENDING,
            ReportNavigationType.PAGE
    );

    AdminReportListResult result = new AdminReportListResult(
            List.of(item),
            1,
            10,
            1L,
            2
    );

    @Test
    @DisplayName("신고 목록 조회 api가 올바른 목록을 반환한다.")
    void 신고_목록_조회_api_올바른_목록_반환_테스트() throws Exception {

        // given
        BDDMockito.given(reportGroupQueryUseCase.findReportGroups(any(AdminReportListQuery.class)))
                .willReturn(result);

        // when, than
        // $ = Json 데이터의 최상위 root 객체
        // $는 responseEntity.ok의 body {}를 의미
        // $.result는 body 속 1개의 data -> 즉 (jsonPath("$.result")는 10을 의미
        // .value(expectedSum)는 우리가 넣은 값
        // andExpect()로 위 2개를 비교해서 일치여부를 판단
        mockMvc.perform(get("/api/reportgroup/list")
                        .param("targetType", "PT_COURSE")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REPORT_200_2"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.reports[0].reportGroupId").value(999))
                .andExpect(jsonPath("$.data.reports[0].reportNumber").value("RPT-001"))
                .andExpect(jsonPath("$.data.reports[0].targetType").value("PT"))
                .andExpect(jsonPath("$.data.reports[0].targetId").value(10))
                .andExpect(jsonPath("$.data.reports[0].targetDisplayText").value("욕설이 난무하는 PT"))
                .andExpect(jsonPath("$.data.reports[0].targetOwnerUsername").value("트레이너 현자"))
                .andExpect(jsonPath("$.data.reports[0].effectiveReportCount").value(3))
                .andExpect(jsonPath("$.data.reports[0].navigationType").value("PAGE"));
    }

    @Test
    @DisplayName("신고 사유 상세 조회 api가 올바른 상세 목록을 반환한다.")
    void 신고_사유_상세_조회_api_올바른_상세_목록_반환_테스트() throws Exception {

        // given
        AdminReportReasonItem report1 = new AdminReportReasonItem(
                100L,
                "회원A",
                ReportReasonType.ABUSE,
                "욕설이 포함되어 있습니다.",
                LocalDateTime.of(2026, 5, 28, 17, 20),
                ReportStatus.PENDING
        );
        AdminReportReasonItem report2 = new AdminReportReasonItem(
                101L,
                "회원B",
                ReportReasonType.SPAM,
                "광고성 내용입니다.",
                LocalDateTime.of(2026, 5, 28, 18, 5),
                ReportStatus.REJECTED
        );
        AdminReportDetailResult detailResult = new AdminReportDetailResult(
                1L,
                ReportGroupReviewStatus.PENDING,
                List.of(report1, report2)
        );
        BDDMockito.given(reportGroupQueryUseCase.findReportDetail(1L))
                        .willReturn(detailResult);

        // when & then
        mockMvc.perform(get("/api/reportgroup/detail/{reportGroupId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REPORT_200_2"))
                .andExpect(jsonPath("$.data.reportGroupId").value(1))
                .andExpect(jsonPath("$.data.reports[0].reportId").value(100))
                .andExpect(jsonPath("$.data.reports[0].reporterUsername").value("회원A"))
                .andExpect(jsonPath("$.data.reports[0].detail").value("욕설이 포함되어 있습니다."));
    }

    @Test
    @DisplayName("신고 사유 상세 조회 api가 404 에러를 반환한다.")
    void 신고_사유_상세_조회_api_404_ERROR_반환_테스트() throws Exception {

        // given
        AdminReportReasonItem report1 = new AdminReportReasonItem(
                100L,
                "회원A",
                ReportReasonType.ABUSE,
                "욕설이 포함되어 있습니다.",
                LocalDateTime.of(2026, 5, 28, 17, 20),
                ReportStatus.PENDING
        );
        AdminReportReasonItem report2 = new AdminReportReasonItem(
                101L,
                "회원B",
                ReportReasonType.SPAM,
                "광고성 내용입니다.",
                LocalDateTime.of(2026, 5, 28, 18, 5),
                ReportStatus.REJECTED
        );
        AdminReportDetailResult detailResult = new AdminReportDetailResult(
                1L,
                ReportGroupReviewStatus.PENDING,
                List.of(report1, report2)
        );
        BDDMockito.given(reportGroupQueryUseCase.findReportDetail(999L))
                .willThrow(new ReportGroupNotFoundException(999L));

        // when, than
        mockMvc.perform(get("/api/reportgroup/detail/{reportGroupId}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
