package com.foryoufamily.api.service;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.dto.request.LoginReqDto;
import com.foryoufamily.api.dto.response.LoginResDto;

public interface MemberService {

    Long join(JoinReqDto joinDto);

    LoginResDto login(LoginReqDto loginReqDto);
}
