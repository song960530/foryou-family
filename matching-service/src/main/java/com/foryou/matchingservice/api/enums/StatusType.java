package com.foryou.matchingservice.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusType {
    WAIT("WAIT"), START("START"), COMPLETE("COMPLETE");

    private final String value;
}
