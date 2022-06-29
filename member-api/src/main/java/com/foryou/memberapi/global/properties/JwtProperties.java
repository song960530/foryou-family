package com.foryou.memberapi.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperties {

    @Getter
    @Value("${config.jwt.secretKey}")
    private String secretKey;

    @Value("${config.jwt.accessValidTime}")
    private long accessValidTime;

    @Value("${config.jwt.refreshValidTime}")
    private long refreshValidTime;

    public long getAccessValidTime() {
        return accessValidTime * 60_000;
    }

    public long getRefreshValidTime() {
        return refreshValidTime * (60_000 * 60 * 24);
    }
}
