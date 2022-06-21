package com.foryoufamily.api.service;

import com.foryoufamily.api.dto.request.JoinReqDto;

public interface MemberService {

    void join(JoinReqDto joinDto);
}
