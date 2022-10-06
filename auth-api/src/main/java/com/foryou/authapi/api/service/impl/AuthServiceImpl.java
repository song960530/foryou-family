package com.foryou.authapi.api.service.impl;

import com.foryou.authapi.api.dto.TokenResDto;
import com.foryou.authapi.api.entity.Token;
import com.foryou.authapi.api.repository.AuthRepository;
import com.foryou.authapi.api.service.AuthService;
import com.foryou.authapi.global.constants.Constants;
import com.foryou.authapi.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String refreshToken = jwtProvider.createRefreshToken();

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
                                .refreshToken(jwtProvider.createRefreshToken())
                                .build())
                );

        return TokenResDto.builder()
                .accessToken(createdToken.getAccessToken())
                .refreshToken(createdToken.getRefreshToken())
                .type(Constants.TOKEN_TYPE)
                .build();
    }
}
