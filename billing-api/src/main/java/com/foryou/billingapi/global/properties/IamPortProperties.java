package com.foryou.billingapi.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamPortProperties {

    @Getter
    @Value("${config.iamport.apikey}")
    private String apiKey;

    @Getter
    @Value("${config.iamport.apisecret}")
    private String apiSecret;
}
