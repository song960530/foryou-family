package com.foryou.partyapi.api.entity;

import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import com.foryou.partyapi.global.converter.BooleanToYNConverter;
import lombok.AccessLevel;
import lombok.Builder;
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
            name = "OTT"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private OttType ott;

    @Column(
            name = "ROLE"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private PartyRole role;

    @Column(
            name = "PROFILE_NAME"
    )
    private String profileName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name = "PARTY_INFO_NO"
    )
    private PartyInfo partyInfo;

    @Column(
            name = "leaveYN"
    )
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean leaveYN;

    @Builder
    public Party(String memberId, OttType ott, PartyRole role, Boolean leaveYN) {
        this.memberId = memberId;
        this.ott = ott;
        this.role = role;
        this.leaveYN = leaveYN;
    }

    public void addPartyInfo(PartyInfo partyInfo) {
        this.partyInfo = partyInfo;
    }
}
