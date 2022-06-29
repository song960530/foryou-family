package com.foryou.memberapi.global.jwt;

import com.foryou.memberapi.global.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.extractToken((HttpServletRequest) request); // 헤더에서 JWT 받기

        if (!token.equals(Constants.DEFAULT_TOKEN_VALUE)) {
            String subject = jwtTokenProvider.extractSubject(token);
            Authentication authentication = jwtTokenProvider.createAuthentication(subject);

            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 Authentication 객체 저장
        }

        chain.doFilter(request, response);
    }
}

