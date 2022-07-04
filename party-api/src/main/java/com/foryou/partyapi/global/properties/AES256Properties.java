package com.foryou.partyapi.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AES256Properties {
    @Value("${config.cipher.aesKey}")
    private String key;
    @Value("${config.cipher.iv}")
    private String iv;
}
