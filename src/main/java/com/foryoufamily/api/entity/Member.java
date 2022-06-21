package com.foryoufamily.api.entity;

import com.foryoufamily.api.entity.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
            name = "ID"
            , unique = true
            , nullable = false
    )
    private String id;

    @Column(
            name = "PASSWORD"
            , nullable = false
    )
    private String password;

    public Member(String id, String password) {
        this.id = id;
        this.password = password;
    }
}