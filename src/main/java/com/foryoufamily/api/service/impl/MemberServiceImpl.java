package com.foryoufamily.api.service.impl;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.dto.request.LoginReqDto;
import com.foryoufamily.api.dto.response.LoginResDto;
import com.foryoufamily.api.entity.Member;
import com.foryoufamily.api.repository.MemberRepository;
import com.foryoufamily.api.service.MemberService;
import com.foryoufamily.global.Constants;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import com.foryoufamily.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public Long join(JoinReqDto joinDto) {
        if (memberRepository.existsByMemberId(joinDto.getMemberId()))
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);

        return memberRepository.save(joinDto.toEntity()).getNo();
    }

    @Override
    public LoginResDto login(LoginReqDto loginReqDto) {
        Member member = validLogin(loginReqDto);

        return LoginResDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRoles()))
                .refreshToken(jwtTokenProvider.createRefreshToken(member.getMemberId()))
                .type(Constants.TOKEN_TYPE)
                .build();
    }

    private Member validLogin(LoginReqDto loginReqDto) {
        return Optional.of(memberRepository.findByMemberId(loginReqDto.getMemberId())
                        .orElseThrow(() -> {
                            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID);
                        }))
                .filter(m -> passwordEncoder.matches(loginReqDto.getPassword(), m.getPassword()))
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
                });
    }
}
