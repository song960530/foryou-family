package com.foryoufamily.api.service;

import com.foryoufamily.api.dto.request.JoinReqDto;

public interface MemberService {

    Long join(JoinReqDto joinDto);
}
