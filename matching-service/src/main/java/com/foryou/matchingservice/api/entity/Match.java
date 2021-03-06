package com.foryou.matchingservice.api.entity;

import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_MATCH_GENERATOR"
        , sequenceName = "SEQ_MATCH"
        , initialValue = 1
        , allocationSize = 1
)
public class Match {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_MATCH_GENERATOR"
    )
    private Long no;

    @Column(
            name = "PARTY_NO"
            , nullable = false
            , updatable = false
    )
    private Long partyNo;

    @Column(
            name = "OTT"
            , updatable = false
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private OttType ott;

    @Column(
            name = "ROLE"
            , updatable = false
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private PartyRole role;

    @Column(
            name = "STATUS"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private StatusType status;

    @Column(name = "LINKED_NO")
    private Long linkedNo;

    @Builder
    public Match(Long partyNo, OttType ott, PartyRole role) {
        this.partyNo = partyNo;
        this.ott = ott;
        this.role = role;
        this.status = StatusType.WAIT;
        this.linkedNo = 0L;
    }

    public void changeStatus(StatusType status) {
        this.status = status;
    }

    public void link(Long no) {
        this.linkedNo = no;
    }
}
