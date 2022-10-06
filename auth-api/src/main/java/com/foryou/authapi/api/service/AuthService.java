package com.foryou.authapi.api.service;

import com.foryou.authapi.api.dto.TokenResDto;

public interface AuthService {

    TokenResDto createOrUpdateToken(String memberId);
}
