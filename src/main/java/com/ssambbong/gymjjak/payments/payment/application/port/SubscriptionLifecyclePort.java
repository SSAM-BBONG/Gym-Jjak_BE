package com.ssambbong.gymjjak.payments.payment.application.port;

public interface SubscriptionLifecyclePort {

    void expire(Long subscriptionId);
}
