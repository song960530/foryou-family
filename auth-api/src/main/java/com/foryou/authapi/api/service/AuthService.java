package com.foryou.authapi.api.service;

import com.foryou.authapi.api.dto.TokenResDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    TokenResDto createOrUpdateToken(String memberId);

    TokenResDto reCreateToken(String memberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
