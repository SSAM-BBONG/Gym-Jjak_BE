package com.ssambbong.gymjjak.payments.payment.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PortOneProperties.class)
public class PortOneConfig {

    @Bean
    public RestClient portOneRestClient(RestClient.Builder builder, PortOneProperties props) {
        return builder
                .baseUrl("https://api.portone.io")
                .defaultHeader("Authorization", "PortOne " + props.getApiSecret())
                .build();
    }
}
