package com.foryou.partyapi.api.entity;

import com.foryou.partyapi.api.enums.OttType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_PARTY_INFO_GENERATOR"
        , sequenceName = "SEQ_PARTY_INFO"
        , initialValue = 1
        , allocationSize = 1
)
@Entity
public class PartyInfo {

    @Id
    @Column(name = "PARTY_INFO_NO")
    @GeneratedValue(
            generator = "SEQ_PARTY_INFO_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "OTT_TYPE"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private OttType ottType;

    @Column(
            name = "PARTY_SHARE_ID"
            , nullable = false
    )
    private String partyShareId;

    @Column(
            name = "PARTY_SHARE_PASSWORD"
            , nullable = false
    )
    private String partySharePassword;
}
