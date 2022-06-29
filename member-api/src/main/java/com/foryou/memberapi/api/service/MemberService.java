package com.foryou.memberapi.api.service;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;

public interface MemberService {

    Long join(JoinReqDto joinDto);

    LoginResDto login(LoginReqDto loginReqDto);
}
