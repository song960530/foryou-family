package com.foryou.partyapi.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum OttType {
    NETFLIX("NETFLIX"), TVING("TVING"), WAVVE("WAVVE"), WATCHA("WATCHA"), DISNEY_PLUS("DISNEY_PLUS");

    private final String value;

    @JsonCreator
    public static OttType from(String input) {

        return Stream.of(OttType.values())
                .filter(value -> value.getValue().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
