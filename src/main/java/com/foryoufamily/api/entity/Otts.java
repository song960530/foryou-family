package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.Ott;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "OTT")
@SequenceGenerator(
        name = "SEQ_OTT_GENERATOR"
        , sequenceName = "SEQ_OTT"
        , initialValue = 1
        , allocationSize = 1
)
public class Otts extends BaseTimeEntity {
    @Id
    @Column(name = "OTT_NO")
    @GeneratedValue(
            generator = "SEQ_OTT_GENERATOR"
            , strategy = GenerationType.SEQUENCE
    )
    private Long no;

    @Column(
            name = "PRODUCT"
            , nullable = false
            , unique = true
    )
    private Ott product;

    @Column(
            name = "PRICE"
            , nullable = false
    )
    private Long price;

    @Column(
            name = "SHARE_PRICE"
            , nullable = false
    )
    private Long sharePrice;
}
