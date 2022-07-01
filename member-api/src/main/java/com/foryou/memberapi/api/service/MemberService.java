package com.foryou.memberapi.api.service;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.dto.response.TokenResDto;

public interface MemberService {

    Long join(JoinReqDto joinDto);

    LoginResDto login(LoginReqDto loginReqDto);

    TokenResDto reCreateToken(String accessToken, String refreshToken);
}
