package com.foryou.billingapi.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {
    PAYMENT("PAYMENT"), ALL_CANCEL("ALL_CANCEL"), PART_CANCEL("PART_CANCEL");

    private final String value;
}
