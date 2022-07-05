package com.foryou.matchingservice.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyRole {
    MEMBER("MEMBER"), OWNER("OWNER");

    private String value;
}
