package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.api.entity.common.BaseTimeEntity;
import com.foryou.billingapi.global.converter.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PRODUCT_GENERATOR"
        , sequenceName = "SEQ_PRODUCT"
        , allocationSize = 1
        , initialValue = 1

)
@Table(name = "PRODUCT")
public class Product extends BaseTimeEntity {
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
    private Payments payment;

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
    private int price;

    @Column(
            name = "CANCEL_YN"
            , nullable = false
    )
    @Convert(converter = BooleanToYNConverter.class)
    private boolean cancelYN;

    @Column(
            name = "JOIN_DATE"
            , nullable = false
            , updatable = false
    )
    private LocalDate joinDate;

    @Column(
            name = "DUE_DATE"
            , nullable = false
    )
    private LocalDate dueDate;

    @Column(
            name = "SUB_MONTHS"
            , nullable = false
    )
    private int subMonth;

    @OneToMany(
            mappedBy = "product"
            , cascade = CascadeType.ALL
    )
    private List<PaymentHistory> paymentHistories = new ArrayList<>();

    @Builder
    public Product(Long partyNo, int price) {
        this.partyNo = partyNo;
        this.price = price;
        this.joinDate = LocalDate.now();
        this.subMonth = 0;
        calcNextDueDate();
    }

    public void calcNextDueDate() {
        this.dueDate = this.joinDate.plusMonths(this.subMonth++);
    }

    public void addPayment(Payments payment) {
        this.payment = payment;
    }

    public void addPaymentHistory(PaymentHistory history) {
        this.paymentHistories.add(history);
        history.addProduct(this);
    }
}
