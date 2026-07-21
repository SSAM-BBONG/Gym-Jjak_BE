package com.ssambbong.gymjjak.report;

import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import com.ssambbong.gymjjak.report.application.query.AdminReportDetailResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportListItem;
import com.ssambbong.gymjjak.report.application.query.AdminReportListQuery;
import com.ssambbong.gymjjak.report.application.query.AdminReportListResult;
import com.ssambbong.gymjjak.report.application.query.AdminReportReasonItem;
import com.ssambbong.gymjjak.report.application.query.AdminReportSnapshotResult;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupCommandUseCase;
import com.ssambbong.gymjjak.report.application.usecase.ReportGroupQueryUseCase;
import com.ssambbong.gymjjak.report.domain.exception.ReportGroupNotFoundException;
import com.ssambbong.gymjjak.report.domain.model.ReportGroupReviewStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportNavigationType;
import com.ssambbong.gymjjak.report.domain.model.ReportReasonType;
import com.ssambbong.gymjjak.report.domain.model.ReportStatus;
import com.ssambbong.gymjjak.report.domain.model.ReportTargetType;
import com.ssambbong.gymjjak.report.presentation.api.ReportGroupController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportGroupController.class)
class ReportGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportGroupCommandUseCase reportGroupCommandUseCase;

    @MockitoBean
    private ReportGroupQueryUseCase reportGroupQueryUseCase;

    /*
     * JwtAuthenticationFilter가 ApplicationContext에 등록될 때
     * 필요한 의존성
     */
    @MockitoBean
    private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    private Authentication adminAuthentication;

    @BeforeEach
    void setUp() {
        AuthUser admin = new AuthUser(
                17L,
                "admin@test.com",
                "ADMIN"
        );

        adminAuthentication =
                new UsernamePasswordAuthenticationToken(
                        admin,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ADMIN")
                        )
                );
    }

    @Test
    @DisplayName("신고 그룹 목록을 0-based 페이지로 조회한다")
    void findReportGroups_success() throws Exception {
        // given
        AdminReportListItem item = new AdminReportListItem(
                999L,
                "RPT-001",
                ReportTargetType.PT_COURSE,
                10L,
                "정상적으로 운영되는 PT",
                "trainer@test.com",
                LocalDateTime.of(2026, 5, 28, 17, 10),
                3,
                ReportGroupReviewStatus.PENDING,
                ReportNavigationType.PAGE
        );

        AdminReportListResult result =
                new AdminReportListResult(
                        List.of(item),
                        0,
                        10,
                        1L,
                        1
                );

        when(reportGroupQueryUseCase.findReportGroups(
                any(AdminReportListQuery.class)
        )).thenReturn(result);

        // when & then
        mockMvc.perform(
                        get("/api/reportgroup/list")
                                .with(authentication(adminAuthentication))
                                .param("targetType", "PT_COURSE")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REPORT_200_2"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(
                        jsonPath("$.data.reports[0].reportGroupId")
                                .value(999)
                )
                .andExpect(
                        jsonPath("$.data.reports[0].reportNumber")
                                .value("RPT-001")
                )
                .andExpect(
                        jsonPath("$.data.reports[0].targetType")
                                .value("PT")
                )
                .andExpect(
                        jsonPath("$.data.reports[0].targetId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.data.reports[0].targetDisplayText")
                                .value("정상적으로 운영되는 PT")
                )
                .andExpect(
                        jsonPath("$.data.reports[0].targetOwnerUsername")
                                .value("trainer@test.com")
                )
                .andExpect(
                        jsonPath("$.data.reports[0].effectiveReportCount")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.data.reports[0].navigationType")
                                .value("PAGE")
                );

        verify(reportGroupQueryUseCase).findReportGroups(
                new AdminReportListQuery(
                        ReportTargetType.PT_COURSE,
                        0,
                        10
                )
        );
    }

    @Test
    @DisplayName("page를 생략하면 기본값 0으로 조회한다")
    void findReportGroups_defaultPage() throws Exception {
        // given
        AdminReportListResult result =
                new AdminReportListResult(
                        List.of(),
                        0,
                        10,
                        0L,
                        0
                );

        when(reportGroupQueryUseCase.findReportGroups(
                any(AdminReportListQuery.class)
        )).thenReturn(result);

        // when & then
        mockMvc.perform(
                        get("/api/reportgroup/list")
                                .with(authentication(adminAuthentication))
                                .param("targetType", "PT_COURSE")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.reports").isArray())
                .andExpect(jsonPath("$.data.reports").isEmpty());

        verify(reportGroupQueryUseCase).findReportGroups(
                new AdminReportListQuery(
                        ReportTargetType.PT_COURSE,
                        0,
                        10
                )
        );
    }

    @Test
    @DisplayName("page가 음수이면 400 응답을 반환한다")
    void findReportGroups_negativePage() throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/reportgroup/list")
                                .with(authentication(adminAuthentication))
                                .param("targetType", "PT_COURSE")
                                .param("page", "-1")
                                .param("size", "10")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportGroupQueryUseCase);
    }

    @Test
    @DisplayName("신고 그룹 상세 정보를 조회한다")
    void findReportDetails_success() throws Exception {
        // given
        AdminReportReasonItem report1 =
                new AdminReportReasonItem(
                        100L,
                        "userA@test.com",
                        ReportReasonType.ABUSE,
                        "부적절한 표현이 포함되어 있습니다.",
                        LocalDateTime.of(2026, 5, 28, 17, 20),
                        ReportStatus.PENDING
                );

        AdminReportReasonItem report2 =
                new AdminReportReasonItem(
                        101L,
                        "userB@test.com",
                        ReportReasonType.SPAM,
                        "광고성 내용입니다.",
                        LocalDateTime.of(2026, 5, 28, 18, 5),
                        ReportStatus.REJECTED
                );

        AdminReportDetailResult result =
                new AdminReportDetailResult(
                        1L,
                        ReportGroupReviewStatus.PENDING,
                        List.of(report1, report2)
                );

        when(reportGroupQueryUseCase.findReportDetail(1L))
                .thenReturn(result);

        // when & then
        mockMvc.perform(
                        get(
                                "/api/reportgroup/detail/{reportGroupId}",
                                1L
                        )
                                .with(authentication(adminAuthentication))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REPORT_200_3"))
                .andExpect(
                        jsonPath("$.data.reportGroupId").value(1)
                )
                .andExpect(
                        jsonPath("$.data.reports[0].reportId").value(100)
                )
                .andExpect(
                        jsonPath("$.data.reports[0].reporterUsername")
                                .value("userA@test.com")
                )
                .andExpect(
                        jsonPath("$.data.reports[0].detail")
                                .value("부적절한 표현이 포함되어 있습니다.")
                );

        verify(reportGroupQueryUseCase).findReportDetail(1L);
    }

    @Test
    @DisplayName("존재하지 않는 신고 그룹을 상세 조회하면 404를 반환한다")
    void findReportDetails_notFound() throws Exception {
        // given
        when(reportGroupQueryUseCase.findReportDetail(999L))
                .thenThrow(new ReportGroupNotFoundException(999L));

        // when & then
        mockMvc.perform(
                        get(
                                "/api/reportgroup/detail/{reportGroupId}",
                                999L
                        )
                                .with(authentication(adminAuthentication))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REPORT_404_1"));

        verify(reportGroupQueryUseCase).findReportDetail(999L);
    }

    @Test
    @DisplayName("관리자가 신고 대상 스냅샷을 조회한다")
    void findReportSnapshot_success() throws Exception {
        // 신고 접수 시점에 저장된 스냅샷을 모달 응답으로 반환합니다.
        AdminReportSnapshotResult result = new AdminReportSnapshotResult(
                10L,
                ReportTargetType.COMMENT,
                301L,
                "댓글",
                "신고된 댓글 내용",
                null
        );
        when(reportGroupQueryUseCase.findReportSnapshot(10L)).thenReturn(result);

        mockMvc.perform(
                        get("/api/reportgroup/{reportGroupId}/snapshot", 10L)
                                .with(authentication(adminAuthentication))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("REPORT_200_10"))
                .andExpect(jsonPath("$.data.reportGroupId").value(10))
                .andExpect(jsonPath("$.data.targetType").value("댓글"))
                .andExpect(jsonPath("$.data.targetId").value(301))
                .andExpect(jsonPath("$.data.title").value("댓글"))
                .andExpect(jsonPath("$.data.content").value("신고된 댓글 내용"))
                .andExpect(jsonPath("$.data.fileUrl").value(nullValue()));

        verify(reportGroupQueryUseCase).findReportSnapshot(10L);
    }

    @Test
    @DisplayName("삭제됐거나 존재하지 않는 신고 그룹의 스냅샷 조회는 404를 반환한다")
    void findReportSnapshot_notFound() throws Exception {
        // 활성 신고 그룹만 조회하므로 삭제된 그룹도 동일하게 404로 처리합니다.
        when(reportGroupQueryUseCase.findReportSnapshot(999L))
                .thenThrow(new ReportGroupNotFoundException(999L));

        mockMvc.perform(
                        get("/api/reportgroup/{reportGroupId}/snapshot", 999L)
                                .with(authentication(adminAuthentication))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REPORT_404_1"));

        verify(reportGroupQueryUseCase).findReportSnapshot(999L);
    }

}
