package com.ssambbong.gymjjak.payments.payment.application.command;

// PT 결제 요청 생성
public record CreatePtPaymentCommand(
        Long userId,
        Long ptCourseId
) {}
