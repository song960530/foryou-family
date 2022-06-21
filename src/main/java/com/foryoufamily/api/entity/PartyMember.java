package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.PartyRole;
import com.foryoufamily.api.enums.JoinStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_PARTY_MEMBER_GENERATOR"
        , sequenceName = "SEQ_PARTY_MEMBER"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "PARTY_MEMBER")
public class PartyMember extends BaseTimeEntity {
    @Id
    @Column(name = "PARTY_MEMBER_NO")
    @GeneratedValue(
            generator = "SEQ_PARTY_MEMBER_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(name = "PROFILE")
    private String profile;

    @Column(
            name = "ROLE"
            , nullable = false
    )
    private PartyRole role;

    @Column(
            name = "STATUS"
            , nullable = false
    )
    private JoinStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "MEMBER_NO"
            , nullable = false
    )
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "PARTY_NO"
            , nullable = false
    )
    private Party party;
}
