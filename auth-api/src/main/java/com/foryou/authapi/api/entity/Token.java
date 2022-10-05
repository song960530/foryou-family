package com.foryou.authapi.api.entity;

import com.foryou.authapi.api.entity.common.BaseTimeEntity;
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
            name = "MEMBER_ID"
            , nullable = false
            , unique = true
    )
    private String memberId;

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

    @Builder
    public Token(String accessToken, String refreshToken, String memberId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

    public void changeAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
