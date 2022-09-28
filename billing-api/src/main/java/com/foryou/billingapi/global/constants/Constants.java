package com.foryou.billingapi.global.constants;

public class Constants {
    public static final String MERCHANT_UID_PREFIX = "FORYOU_ORDER";
    public static final String PG_TYPE_KCP = "kcp";
    public static final String KCP_STORE_ID = "BA001";

    public static final String COMMA = ".";
    public static final String UNDER_BAR = "_";

    public static final String CHECK_CARD = "정상카드확인";
    public static final String PAYMENT_AMOUNT_MISMATCH = "결제 금액 불일치";

    public static final String KAFKA_TOPIC_PARTY = "payment";
    public static final String KAFKA_GROPU_ID_PAYMENT = "payment_group";
    public static final String KAFKA_AUTO_OFFSET_RESET_EARLIEST = "earliest";
    public static final String KAFKA_TOPIC_PAYMENT_RESULT = "payment_result";
}
