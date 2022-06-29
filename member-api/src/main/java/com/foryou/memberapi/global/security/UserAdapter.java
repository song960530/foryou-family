package com.foryou.memberapi.global.security;

import com.foryou.memberapi.api.entity.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

@Getter
public class UserAdapter extends User {

    private Member member;

    public UserAdapter(Member member) {
        super(member.getMemberId()
                , member.getPassword()
                , member.getRoles().stream()
                        .map(m -> new SimpleGrantedAuthority(m.getRole().name()))
                        .collect(Collectors.toList())
        );

        this.member = member;
    }
}
