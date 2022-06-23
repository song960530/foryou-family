package com.foryoufamily.api.entity;

import com.foryoufamily.api.enums.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "ROLE", unique = true, nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    @ManyToMany
    @JoinTable(
            name = "MEMBER_ROLE"
            , joinColumns = @JoinColumn(name = "ROLE_NO")
            , inverseJoinColumns = @JoinColumn(name = "MEMBER_NO")
    )
    private List<Member> members = new ArrayList<>();

    @Builder
    public Role(MemberRole role) {
        this.role = role;
    }
}
