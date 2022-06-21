package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.Bank;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "ACCOUNT")
@SequenceGenerator(
        name = "SEQ_ACCOUNT_GENERATOR"
        , sequenceName = "SEQ_ACCOOUNT"
        , initialValue = 1
        , allocationSize = 1
)
public class Account extends BaseTimeEntity {

    @Id
    @Column(name = "ACCOUNT_NO")
    @GeneratedValue(
            generator = "SEQ_ACCOUNT_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "ENC_ACCOUNT"
            , nullable = false
            , unique = true
    )
    private String encAccount;

    @Column(
            name = "BANK"
            , nullable = false
    )
    private Bank bank;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO"
            , unique = true
            , nullable = false
    )
    private Member member;
}
