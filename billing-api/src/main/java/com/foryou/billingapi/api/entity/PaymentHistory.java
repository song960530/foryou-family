package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.api.enums.PaymentType;
import com.foryou.billingapi.global.converter.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PAYMENT_HISTORY_GENERATOR"
        , sequenceName = "SEQ_PAYMENT_HISTORY"
        , allocationSize = 1
        , initialValue = 1

)
@Table(name = "PAYMENT_HISTORY")
public class PaymentHistory {
    @Id
    @Column(name = "PAYMENT_HISTORY_NO")
    @GeneratedValue(
            generator = "SEQ_PAYMENT_HISTORY_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @JoinColumn(
            name = "PRODUCT_NO"
            , nullable = false
            , updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(
            name = "PAYMENT_TYPE"
            , nullable = false
            , updatable = false
    )
    private PaymentType status;

    @Column(
            name = "PRICE"
            , nullable = false
            , updatable = false
    )
    private Long price;

    @Column(
            name = "SUCCESS_YN"
            , nullable = false
    )
    @Convert(converter = BooleanToYNConverter.class)
    private boolean successYN;

    @Lob
    @Column(
            name = "RESPONSE"
            , nullable = false
            , updatable = false
    )
    private String response;
}
