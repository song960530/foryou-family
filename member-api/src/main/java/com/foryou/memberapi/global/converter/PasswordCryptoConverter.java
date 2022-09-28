package com.foryou.memberapi.global.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter
@RequiredArgsConstructor
public class PasswordCryptoConverter implements AttributeConverter<String, String> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String convertToDatabaseColumn(String plainText) {
        return Optional.ofNullable(plainText)
                .map(text -> passwordEncoder.encode(text))
                .orElse(null);
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        return Optional.ofNullable(encrypted)
                .orElse(null);
    }
}
