package com.foryoufamily.api.entity;

import com.foryoufamily.api.entity.common.BaseTimeEntity;
import com.foryoufamily.global.crypto.PasswordCryptoConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_MEMBER_GENERATOR"
        , sequenceName = "SEQ_MEMBER"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "MEMBER")
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "MEMBER_NO")
    @GeneratedValue(
            generator = "SEQ_MEMBER_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "USER_ID"
            , unique = true
            , nullable = false
    )
    private String memberId;

    @Column(
            name = "PASSWORD"
            , nullable = false
    )
    @Convert(converter = PasswordCryptoConverter.class)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "MEMBER_ROLE"
            , joinColumns = @JoinColumn(name = "MEMBER_NO")
            , inverseJoinColumns = @JoinColumn(name = "ROLE_NO")
    )
    private List<Role> roles = new ArrayList<>();

    @Builder
    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
    }
}