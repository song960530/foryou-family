package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_ACCOUNT_HISTORY_GENERATOR"
        , sequenceName = "SEQ_ACCOUNT_HISTORY"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "ACCOUNT_HISTORY")
public class AccountHistory extends BaseTimeEntity {
    @Id
    @Column(name = "ACCOUNT_HISTORY_NO")
    @GeneratedValue(
            generator = "SEQ_ACCOUNT_HISTORY_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(name = "MESSAGE")
    private String message;

    @Column(
            name = "REFUND"
            , nullable = false
    )
    private Integer refund;

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
            name = "ACCOUNT_NO"
            , nullable = false
    )
    private Account account;
}
