package com.ssambbong.gymjjak.payments.subscription.presentation.api.response;

import com.ssambbong.gymjjak.global.presentation.api.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubscriptionResponseCode implements ResponseCode {

    SUBSCRIPTION_PLANS_FETCHED("SUBSCRIPTION_PLANS_FETCHED", "구독 플랜 목록 조회 성공");

    private final String code;
    private final String message;
}
