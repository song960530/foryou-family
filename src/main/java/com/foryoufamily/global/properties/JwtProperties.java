package com.foryoufamily.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperties {

    @Getter
    @Value("${config.jwt.secretKey}")
    private String secretKey;

    @Value("${config.jwt.validTime}")
    private long validTime;

    public long getValidTime() {
        return validTime * 60_000L;
    }
}
