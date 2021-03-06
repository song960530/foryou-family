package com.foryou.memberapi.global.jwt;

import com.foryou.memberapi.api.entity.Role;
import com.foryou.memberapi.api.enums.MemberRole;
import com.foryou.memberapi.global.constants.Constants;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import com.foryou.memberapi.global.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private String encSecretKey;

    @PostConstruct
    public void init() {
        encSecretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    // Access 토큰 생성
    public String createAccessToken(String memberId, List<Role> roles) {
        Map<String, Object> headers = createHeader();
        Claims claims = createClaims(memberId, roles);
        Date now = new Date();

        return Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발생 시간 정보
                .setExpiration(new Date(now.getTime() + jwtProperties.getAccessValidTime())) // 만료시간
                .signWith(Keys.hmacShaKeyFor(encSecretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256) // 암호화 및 encSecretKey 세팅
                .compact();
    }

    // Refresh 토큰 생성
    public String createRefreshToken(String memberId) {
        Map<String, Object> headers = createHeader();
        Claims claims = createClaims(memberId);
        Date now = new Date();

        return Jwts.builder()
                .setHeader(headers) // 헤더 설정
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발생 시간 정보
                .setExpiration(new Date(now.getTime() + jwtProperties.getRefreshValidTime())) // 만료시간
                .signWith(Keys.hmacShaKeyFor(encSecretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256) // 암호화 및 encSecretKey 세팅
                .compact();
    }

    private Claims createClaims(String memberId, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(memberId); // JWT payload에 저장되는 정보단위
        List<MemberRole> memberRoles = roles.stream().map(Role::getRole).collect(Collectors.toList());
        claims.put("roles", memberRoles);
        return claims;
    }

    private Claims createClaims(String memberId) {
        Claims claims = Jwts.claims().setSubject(memberId); // JWT payload에 저장되는 정보단위
        return claims;
    }

    private Map<String, Object> createHeader() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
        return headers;
    }

    public String extractSubject(String token) {
        String subject = null;

        try {
            subject = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(encSecretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(removeTokenPrefix(token))
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN_VALUE);
        }

        return subject;
    }

    public String removeTokenPrefix(String token) {
        return token.replaceAll(Constants.TOKEN_PREFIX_REGEX + "( )*", "");
    }

    public boolean isMatchedPrefix(String token) {
        return Pattern.matches(Constants.TOKEN_PREFIX_REGEX + " .*", token);
    }
}
