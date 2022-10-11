package com.foryou.authapi.api.service.impl;

import com.foryou.authapi.api.dto.TokenResDto;
import com.foryou.authapi.api.entity.Token;
import com.foryou.authapi.api.repository.AuthRepository;
import com.foryou.authapi.api.service.AuthService;
import com.foryou.authapi.global.constants.Constants;
import com.foryou.authapi.global.error.CustomException;
import com.foryou.authapi.global.error.ErrorCode;
import com.foryou.authapi.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtProvider;
    private final AuthRepository repository;

    @Override
    @Transactional
    public TokenResDto createOrUpdateToken(String memberId) {
        String accessToken = jwtProvider.createAccessToken(memberId);
        String refreshToken = jwtProvider.createRefreshToken(memberId);

        Token createdToken = Optional.of(repository.findByMemberId(memberId))
                .filter(token -> !token.isEmpty())
                .flatMap(token -> {
                    token.get().changeAccessToken(accessToken);
                    token.get().changeRefreshToken(refreshToken);

                    return token;
                })
                .orElseGet(() -> repository.save(
                        Token.builder()
                                .memberId(memberId)
                                .accessToken(jwtProvider.createAccessToken(memberId))
                                .refreshToken(jwtProvider.createRefreshToken(memberId))
                                .build())
                );

        return TokenResDto.builder()
                .accessToken(createdToken.getAccessToken())
                .refreshToken(createdToken.getRefreshToken())
                .type(Constants.TOKEN_TYPE)
                .build();
    }

    @Override
    @Transactional
    public TokenResDto reCreateToken(String memberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = Optional.ofNullable(httpServletRequest.getCookies())
                .filter(cookies1 -> cookies1 != null)
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOt_EXIST_REFRESH_TOKEN);
                });

        Optional<Cookie> refreshCookie = Arrays.stream(cookies)
                .filter(cookie1 -> Constants.REFRESH_TOKEN_HEADER_NAME.equals(cookie1.getName()))
                .findFirst();

        if (refreshCookie.isEmpty())
            throw new CustomException(ErrorCode.NOt_EXIST_REFRESH_TOKEN);


        return refreshCookie
                .map(cookie -> cookie.getValue())
                .map(refreshToken ->
                        repository.findByMemberIdAndRefreshToken(memberId, refreshToken)
                                .orElseThrow(() -> {
                                    throw new CustomException(ErrorCode.ARGUMENT_NOT_VALID);
                                })
                )
                .map(token -> {
                    token.changeAccessToken(jwtProvider.createAccessToken(memberId));
                    token.changeRefreshToken(jwtProvider.createRefreshToken(memberId));

                    setRefreshTokenInCookie(token.getRefreshToken(), httpServletResponse);

                    return TokenResDto
                            .builder()
                            .accessToken(token.getAccessToken())
                            .refreshToken("httponly")
                            .type(Constants.TOKEN_TYPE)
                            .build();
                })
                .get();
    }

    private void setRefreshTokenInCookie(String refreshToken, HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_HEADER_NAME, refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");

        httpServletResponse.addCookie(cookie);
    }
}
