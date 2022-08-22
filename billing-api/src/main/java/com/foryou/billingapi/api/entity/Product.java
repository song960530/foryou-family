package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.global.converter.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PRODUCT_GENERATOR"
        , sequenceName = "SEQ_PRODUCT"
        , allocationSize = 1
        , initialValue = 1

)
@Table(name = "PRODUCT")
public class Product {
    @Id
    @Column(name = "PRODUCT_NO")
    @GeneratedValue(
            generator = "SEQ_PRODUCT_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @JoinColumn(
            name = "PAYMENT_NO"
            , nullable = false
            , updatable = false
    )
    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @Column(
            name = "PARTY_NO"
            , nullable = false
            , updatable = false
    )
    private Long partyNo;

    @Column(
            name = "PRICE"
            , nullable = false
    )
    private Long price;

    @Column(
            name = "CANCEL_YN"
            , nullable = false
    )
    @Convert(converter = BooleanToYNConverter.class)
    private boolean cancelYN;

    @Column(
            name = "DUE_DATE"
            , nullable = false
    )
    private LocalDateTime dueDate;

    @OneToMany(
            mappedBy = "product"
            , cascade = CascadeType.ALL
    )
    private List<PaymentHistory> paymentHistories = new ArrayList<>();
}
