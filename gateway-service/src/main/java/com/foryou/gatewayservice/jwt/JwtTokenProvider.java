package com.foryou.gatewayservice.jwt;

import com.foryou.gatewayservice.constants.Constants;
import com.foryou.gatewayservice.exception.CustomException;
import com.foryou.gatewayservice.exception.ErrorCode;
import com.foryou.gatewayservice.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Base64.getEncoder()
                .encodeToString(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8));
    }

    public String extractToken(ServerWebExchange request) {

        return Optional
                .ofNullable(request.getRequest().getHeaders().get(Constants.TOKEN_HEADER_NAME))
                .map(r -> r.get(0))
                .or(() -> Optional.of(Constants.TOKEN_TYPE + " " + Constants.DEFAULT_TOKEN_VALUE))
                .filter(this::isMatchedPrefix)
                .map(this::removeTokenPrefix)
                .orElseThrow(() -> {
                    throw new CustomException(ErrorCode.NOT_VALID_TOKEN_FORM);
                });
    }

    private boolean isMatchedPrefix(String token) {
        return Pattern.matches(Constants.TOKEN_PREFIX_REGEX + " .*", token);
    }

    private String removeTokenPrefix(String token) {
        return token.replaceAll(Constants.TOKEN_PREFIX_REGEX + "( )*", "");
    }

    public Claims extractClaims(String token) {
        Claims claims;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN_VALUE);
        }

        return claims;
    }
}
