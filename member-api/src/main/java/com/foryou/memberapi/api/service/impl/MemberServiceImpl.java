package com.foryou.memberapi.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.dto.response.TokenResDto;
import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.entity.Token;
import com.foryou.memberapi.api.repository.MemberRepository;
import com.foryou.memberapi.api.repository.TokenRepository;
import com.foryou.memberapi.api.service.MemberService;
import com.foryou.memberapi.global.constants.Constants;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import com.foryou.memberapi.global.jwt.JwtTokenProvider;
import com.foryou.memberapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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
    public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse httpServletResponse) {
        Member member = validLogin(loginReqDto.getMemberId(), loginReqDto.getPassword());

        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("http://localhost:8094/auth/" + member.getMemberId(), null, String.class);

        return Optional.of(stringResponseEntity)
                .filter(response -> response.getStatusCode() == HttpStatus.OK)
                .map(response -> convertLoginResult(httpServletResponse, response))
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.LOGIN_FAIL_ERROR);
                });
    }

    private LoginResDto convertLoginResult(HttpServletResponse httpServletResponse, ResponseEntity<String> response) {
        LoginResDto loginResDto = null;

        try {
            loginResDto = objectMapper.convertValue(objectMapper.readValue(response.getBody(), ApiResponse.class).getData(), LoginResDto.class);
            setRefreshTokenInCookie(loginResDto.getRefreshToken(), httpServletResponse);
            loginResDto.setRefreshToken("httponly");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return loginResDto;
    }

    private void setRefreshTokenInCookie(String refreshToken, HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_HEADER_NAME, refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");

        httpServletResponse.addCookie(cookie);
    }

    @Override
    @Transactional
    public TokenResDto reCreateToken(String accessToken, String refreshToken) {
        validHeaderToken(accessToken, refreshToken);
        Token token = findBeforeToken(accessToken, refreshToken);
        Token reMake = createToken(token.getMember());

        return TokenResDto.builder()
                .accessToken(reMake.getAccessToken())
                .refreshToken(reMake.getRefreshToken())
                .type(Constants.TOKEN_TYPE)
                .build();
    }

    @Transactional
    protected Token createToken(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId(), member.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());

        return Optional.of(tokenRepository.findByMember(member))
                .filter(token -> !token.isEmpty())
                .flatMap(token -> {
                    token.get().changeAccessToken(accessToken);
                    token.get().changeRefreshToken(refreshToken);

                    return token;
                })
                .orElseGet(() -> tokenRepository.save(Token.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .member(member)
                        .build())
                );
    }

    private Token findBeforeToken(String accessToken, String refreshToken) {
        String memberId = jwtTokenProvider.extractSubject(refreshToken);

        return tokenRepository.findByAllColumn(memberId
                        , jwtTokenProvider.removeTokenPrefix(accessToken)
                        , jwtTokenProvider.removeTokenPrefix(refreshToken)
                )
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);
                });
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

    private void validHeaderToken(String accessToken, String refreshToken) {
        if (!jwtTokenProvider.isMatchedPrefix(accessToken) || !jwtTokenProvider.isMatchedPrefix(refreshToken))
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN_FORM);
    }
}
