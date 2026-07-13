package com.ssambbong.gymjjak.payments.payment.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssambbong.gymjjak.global.application.auth.port.in.AuthenticateAccessTokenUseCase;
import com.ssambbong.gymjjak.global.presentation.security.JwtAuthenticationConverter;
import com.ssambbong.gymjjak.payments.payment.application.command.ProcessWebhookCommand;
import com.ssambbong.gymjjak.payments.payment.application.usecase.PaymentCommandUseCase;
import com.ssambbong.gymjjak.payments.payment.domain.exception.PaymentNotFoundException;
import com.ssambbong.gymjjak.payments.payment.presentation.api.request.WebhookPaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentWebhookController.class)
class PaymentWebhookControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PaymentCommandUseCase paymentCommandUseCase;
    @MockitoBean private AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean private JwtAuthenticationConverter jwtAuthenticationConverter;

    private static final String URL = "/api/payments/webhook";

    private WebhookPaymentRequest validRequest() {
        return new WebhookPaymentRequest(
                "Transaction.Paid",
                new WebhookPaymentRequest.WebhookData("portone-abc", "PT-TEST0001")
        );
    }

    @Test
    @DisplayName("정상 웹훅 수신 시 200과 WEBHOOK_RECEIVED 코드를 반환한다")
    void receiveWebhook_success() throws Exception {
        doNothing().when(paymentCommandUseCase).processWebhook(any(ProcessWebhookCommand.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("WEBHOOK_RECEIVED"))
                .andExpect(jsonPath("$.message").value("웹훅 처리 완료"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("존재하지 않는 orderId 웹훅 수신 시 404와 PAYMENT_002 코드를 반환한다")
    void receiveWebhook_orderNotFound_returns404() throws Exception {
        doThrow(new PaymentNotFoundException())
                .when(paymentCommandUseCase).processWebhook(any(ProcessWebhookCommand.class));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("PAYMENT_002"))
                .andExpect(jsonPath("$.message").value("결제 정보를 찾을 수 없습니다."));
    }
}
