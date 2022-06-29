package com.foryou.memberapi.api.service.impl;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.repository.MemberRepository;
import com.foryou.memberapi.api.service.MemberService;
import com.foryou.memberapi.global.constants.Constants;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import com.foryou.memberapi.global.jwt.JwtTokenProvider;
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
        checkExistMember(joinDto.getMemberId());

        return memberRepository.save(joinDto.toEntity()).getNo();
    }

    private void checkExistMember(String memberId) {
        if (memberRepository.existsByMemberId(memberId))
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
    }
    
    @Override
    public LoginResDto login(LoginReqDto loginReqDto) {
        Member member = validLogin(loginReqDto.getMemberId(), loginReqDto.getPassword());

        return LoginResDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRoles()))
                .refreshToken(jwtTokenProvider.createRefreshToken(member.getMemberId()))
                .type(Constants.TOKEN_TYPE)
                .build();
    }

    private Member validLogin(String memberId, String password) {
        return Optional.of(memberRepository.findByMemberId(memberId)
                        .orElseThrow(() -> {
                            throw new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID);
                        }))
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_MATCHED_PASSWORD);
                });
    }
}
