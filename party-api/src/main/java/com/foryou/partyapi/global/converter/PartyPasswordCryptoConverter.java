package com.foryou.partyapi.global.converter;

import com.foryou.partyapi.global.AES256Util;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter
@RequiredArgsConstructor
public class PartyPasswordCryptoConverter implements AttributeConverter<String, String> {

    private final AES256Util aes256Util;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return Optional.ofNullable(attribute)
                .map(plainText -> aes256Util.encrypt(plainText))
                .orElse(null);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .map(encryptText -> aes256Util.decrypt(encryptText))
                .orElse(null);
    }
}
