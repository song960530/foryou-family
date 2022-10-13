package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.api.entity.common.BaseTimeEntity;
import com.foryou.billingapi.global.converter.BooleanToYNConverter;
import com.foryou.billingapi.global.converter.CardDigitConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PAYMENT_GENERATOR"
        , sequenceName = "SEQ_PAYMENT"
        , allocationSize = 1
        , initialValue = 1

)
@Table(name = "PAYMENT")
public class Payments extends BaseTimeEntity {
    @Id
    @Column(name = "PAYMENT_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_PAYMENT_GENERATOR"
    )
    private Long no;

    @Column(
            name = "MEMBER_ID"
            , nullable = false
            , updatable = false
    )
    private String memberId;

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
    @Convert(converter = CardDigitConverter.class)
    private String cardNum4Digit;

    @Column(
            name = "delYN"
            , nullable = false
    )
    @Convert(converter = BooleanToYNConverter.class)
    private boolean delYN;

    @OneToMany(
            mappedBy = "payment"
            , cascade = CascadeType.ALL
    )
    private List<Product> products = new ArrayList<>();

    @Builder
    public Payments(String memberId, String customerUid, String cardNum4Digit) {
        this.memberId = memberId;
        this.customerUid = customerUid;
        this.cardNum4Digit = cardNum4Digit;
        this.delYN = false;
    }

    public void delete() {
        this.delYN = true;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        product.addPayment(this);
    }
}
