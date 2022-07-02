package com.foryou.partyapi.api.entity;

import com.foryou.partyapi.api.enums.PartyRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PARTY_GENERATOR"
        , sequenceName = "SEQ_PARTY"
        , initialValue = 1
        , allocationSize = 1
)
@Entity
public class Party {

    @Id
    @Column(name = "PARTY_NO")
    @GeneratedValue(
            generator = "SEQ_PARTY_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "MEMBER_ID"
            , nullable = false
    )
    private String memberId;

    @Column(
            name = "PROFILE_NAME"
            , columnDefinition = "default '아무개'"
    )
    private String profileName;

    @Column(
            name = "ROLE"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private PartyRole role;

    @OneToOne
    @JoinColumn(
            name = "PARTY_INFO_NO"
            , columnDefinition = "default -99"
    )
    private PartyInfo partyInfo;
}
