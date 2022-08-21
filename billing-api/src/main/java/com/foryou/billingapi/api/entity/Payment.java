package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.global.converter.converter.BooleanToYNConverter;

import javax.persistence.*;

@Entity
@SequenceGenerator(
        name = "SEQ_PAYMENT_GENERATOR"
        , sequenceName = "SEQ_PAYMENT"
        , allocationSize = 1
        , initialValue = 1

)
@Table(name = "PAYMENT")
public class Payment {
    @Id
    @Column(name = "PAYMENT_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_PAYMENT_GENERATOR"
    )
    private Long no;

    @Column(
            name = "USER_ID"
            , nullable = false
            , updatable = false
    )
    private String userId;

    @Column(
            name = "CUSTOMER_UID"
            , unique = true
            , nullable = false
            , updatable = false
    )
    private String customerUid;

    @Column(
            name = "CARD_NUM_4DIGIT"
            , updatable = false
            , nullable = false
    )
    private String cardNum4Digit;

    @Column(
            name = "delYN"
            , nullable = false
    )
    @Convert(converter = BooleanToYNConverter.class)
    private boolean delYN;

}
