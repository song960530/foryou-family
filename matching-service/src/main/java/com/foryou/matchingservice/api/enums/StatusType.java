package com.foryou.matchingservice.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusType {
    WAIT("WAIT"), START("START"), COMPLETE("COMPLETE"), ALL_COMPLETE("ALL_COMPLETE"), CANCEL("CANCEL");

    private final String value;
}
