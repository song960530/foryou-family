package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.PartyRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(
        name = "SEQ_CHARGE_GENERATOR"
        , sequenceName = "SEQ_CHARGE"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "CHARGE")
public class Charge extends BaseTimeEntity {
    @Id
    @Column(name = "CHARGE_NO")
    @GeneratedValue(
            generator = "SEQ_CHARGE_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "ROLE"
            , nullable = false
            , unique = true
    )
    private PartyRole role;

    @Column(
            name = "CHARGE"
            , nullable = false
    )
    private Integer charge;
}
