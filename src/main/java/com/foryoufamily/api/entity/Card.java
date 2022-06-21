package com.foryoufamily.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_CARD_GENERATOR"
        , sequenceName = "SEQ_CARD"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "CARD")
public class Card extends BaseTimeEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_CARD_GENERATOR"
    )
    @Column(name = "CARD_NO")
    private Long no;

    @Column(
            name = "ENC_CARD_NUM"
            , unique = true
            , nullable = false
    )
    private String encCardNum;

    @Column(
            name = "EXPIRED_DATE"
            , nullable = false
    )
    private String expiredDate;

    @Column(
            name = "ENC_FRONT_PW"
            , nullable = false
    )
    private String encFrontPw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "MEMBER_NO"
            , nullable = false)
    private Member member;
}
