package com.ssambbong.gymjjak.payments.payment.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

// yml 포트원 값들을 Java 객체로 바인딩
@Setter
@Getter
@ConfigurationProperties(prefix = "portone")
public class PortOneProperties {

    private String storeId;
    private String apiKey;
    private String apiSecret;
    private String channelKey;
    private String webhookSecret;
}
