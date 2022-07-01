package com.foryou.memberapi.api.entity;

import com.foryou.memberapi.api.enums.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_ROLE_GENERATOR"
        , sequenceName = "SEQ_ROLE"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "ROLE")
public class Role {

    @Id
    @Column(name = "ROLE_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_ROLE_GENERATOR"
    )
    private Long no;

    @Column(
            name = "ROLE"
            , nullable = false
    )
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    @ManyToOne
    @JoinColumn(
            name = "MEMBER_NO"
            , nullable = false
    )
    private Member member;

    @Builder
    public Role(MemberRole role) {
        this.role = role;
    }

    public void addMember(Member member) {
        this.member = member;
    }
}
