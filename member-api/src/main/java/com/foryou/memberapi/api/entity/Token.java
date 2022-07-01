package com.foryou.memberapi.api.entity;

import com.foryou.memberapi.api.entity.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_TOKEN_GENERATOR"
        , sequenceName = "SEQ_MEMBER"
        , initialValue = 1
        , allocationSize = 1
)
public class Token extends BaseTimeEntity {

    @Id
    @Column
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_TOKEN_GENERATOR"
    )
    private Long no;

    @Column(
            name = "ACCESS_TONEK"
            , nullable = false
    )
    private String accessToken;

    @Column(
            name = "REFRESH_TONEK"
            , nullable = false
    )
    private String refreshToken;

    @OneToOne
    @JoinColumn(
            name = "MEMBER_NO"
            , nullable = false
            , unique = true
    )
    private Member member;

    @Builder
    public Token(String accessToken, String refreshToken, Member member) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.member = member;
    }

    public void changeAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
