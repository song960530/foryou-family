package com.foryou.billingapi.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OttType {
    NETFLIX("NETFLIX", BigDecimal.valueOf(4250)),
    TVING("TVING", BigDecimal.valueOf(3475)),
    WAVVE("WAVVE", BigDecimal.valueOf(3475)),
    WATCHA("WATCHA", BigDecimal.valueOf(3225)),
    DISNEY_PLUS("DISNEY_PLUS", BigDecimal.valueOf(2475));

    public static OttType valueOfOtt(OttType param) {
        return Arrays.stream(values())
                .filter(ott -> ott.equals(param))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal calcPrice() {
        return cost.add(fee);
    }

    private final String value;
    private final BigDecimal cost;
    private final BigDecimal fee = BigDecimal.valueOf(1000);
}
