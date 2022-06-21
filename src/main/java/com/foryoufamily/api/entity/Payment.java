package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_PAYMENT_GENERATOR"
        , sequenceName = "SEQ_PAYMENT"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "PAYMENT")
public class Payment extends BaseTimeEntity {
    @Id
    @Column(name = "PAYMENT_NO")
    @GeneratedValue(
            generator = "SEQ_PAYMENT_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "PRICE"
            , nullable = false
    )
    private Integer price;

    @Column(
            name = "PAYMENT_DATE"
            , nullable = false
    )
    @CreatedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime paymentDate;

    @Column(
            name = "STATUS"
            , nullable = false
    )
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "MEMBER_NO"
            , nullable = false
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CARD_NO"
            , nullable = false
    )
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "PARTY_NO"
            , nullable = false
    )
    private Party party;
}
