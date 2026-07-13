package com.ssambbong.gymjjak.payments.payment.presentation.api;

import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.AuthUser;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentQueryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PaymentCommandUseCase paymentCommandUseCase;
    @MockitoBean private PaymentQueryUseCase paymentQueryUseCase;
    @MockitoBean private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean private JwtAuthenticationConverter jwtAuthenticationConverter;

    private Authentication userAuth;

    @BeforeEach
    void setUp() {
        userAuth = new UsernamePasswordAuthenticationToken(
                new AuthUser(1L, "test1234", "USER"), null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
    }

    @Nested
    @DisplayName("GET /api/payments/pt-courses/{ptCourseId}/my-status - PT 구매 상태 조회")
    class GetPtPurchaseStatus {

        private static final String URL = "/api/payments/pt-courses/{ptCourseId}/my-status";

        @Test
        @DisplayName("구매한 PT 코스이면 200과 isPurchased=true를 반환한다")
        void purchased_returnsTrue() throws Exception {
            when(paymentQueryUseCase.isPtCoursePurchased(anyLong(), anyLong())).thenReturn(true);

            mockMvc.perform(get(URL, 10L).with(authentication(userAuth)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("PT_PURCHASE_STATUS_FETCHED"))
                    .andExpect(jsonPath("$.data.isPurchased").value(true));
        }

        @Test
        @DisplayName("구매하지 않은 PT 코스이면 200과 isPurchased=false를 반환한다")
        void notPurchased_returnsFalse() throws Exception {
            when(paymentQueryUseCase.isPtCoursePurchased(anyLong(), anyLong())).thenReturn(false);

            mockMvc.perform(get(URL, 10L).with(authentication(userAuth)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("PT_PURCHASE_STATUS_FETCHED"))
                    .andExpect(jsonPath("$.data.isPurchased").value(false));
        }

    }
}
