package com.foryou.gatewayservice.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperties {

    @Getter
    @Value("${config.jwt.secretKey}")
    private String secretKey;
}
