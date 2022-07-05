package com.foryou.matchingservice.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OttType {
    NETFLIX("NETFLIX"), TVING("TVING"), WAVVE("WAVVE"), WATCHA("WATCHA"), DISNEY_PLUS("DISNEY_PLUS");

    private final String value;
}
