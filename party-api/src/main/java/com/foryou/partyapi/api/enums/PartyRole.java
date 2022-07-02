package com.foryou.partyapi.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum PartyRole {
    MEMBER("MEMBER"), OWNER("OWNER");

    private String value;

    @JsonCreator
    public static PartyRole from(String input) {

        return Stream.of(PartyRole.values())
                .filter(value -> value.getValue().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
