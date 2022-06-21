package com.foryoufamily.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_PARTY_GENERATOR"
        , sequenceName = "SEQ_PARTY"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "PARTY")
public class Party extends BaseTimeEntity {
    @Id
    @Column(name = "PARTY_NO")
    @GeneratedValue(
            generator = "SEQ_PARTY_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "PERSONNEL"
            , nullable = false
    )
    private Integer personnel;

    @Column(
            name = "ID"
            , nullable = false
    )
    private String id;

    @Column(
            name = "PASSWORD"
            , nullable = false
    )
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "OTT_NO"
            , nullable = false
    )
    private Otts ott;

    @OneToMany(mappedBy = "party")
    private List<PartyMember> partyMemberList = new ArrayList<>();
}
