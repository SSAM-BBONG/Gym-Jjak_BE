package com.ssambbong.gymjjak.global.infrastructure.filter;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceIdFilterTest {

    private static class CapturingFilterChain extends MockFilterChain {
        String traceIdInChain;

        @Override
        public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) {
            this.traceIdInChain = MDC.get("traceId");
        }
    }

    private final TraceIdFilter traceIdFilter = new TraceIdFilter();

    @AfterEach
    void clearMDC() {
        MDC.clear();
    }

    @DisplayName("요청 헤더에 traceId가 없으면 새 traceId를 생성하고 응답 헤더에 담는다")
    @Test
    void 요청_헤더_traceId_없으면_새롭게_생성_테스트() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        traceIdFilter.doFilter(request, response, chain);

        String traceId = response.getHeader("X-Trace-Id");

        assertThat(traceId).isNotBlank();
    }

    @DisplayName("요청 헤더에 traceId가 있으면 해당 값을 그대로 사용한다")
    @Test
    void 요청_헤더_traceId_있으면_그대로_사용_테스트() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        traceIdFilter.doFilter(request, response, filterChain);

        assertThat(response.getHeader("X-Trace-Id")).isNotBlank();
        assertThat(response.getHeader("X-Trace-Id")).hasSize(8);
    }

    @DisplayName("필터 실행 중에는 MDC에 traceId가 저장되고, 종료 후에는 제거된다")
    @Test
    void storeTraceIdInMdcAndClearAfterRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        CapturingFilterChain filterChain = new CapturingFilterChain();

        traceIdFilter.doFilter(request, response, filterChain);

        assertThat(filterChain.traceIdInChain).isNotBlank();
        assertThat(MDC.get("traceId")).isNull();
        assertThat(response.getHeader("X-Trace-Id")).isEqualTo(filterChain.traceIdInChain);
    }
}
