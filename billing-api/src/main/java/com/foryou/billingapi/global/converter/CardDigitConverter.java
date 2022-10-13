package com.foryou.billingapi.global.converter;

import com.foryou.billingapi.global.crypto.AES256Util;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@RequiredArgsConstructor
public class CardDigitConverter implements AttributeConverter<String, String> {

    private final AES256Util aes256Util;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return aes256Util.decrypt(dbData);
    }
}
