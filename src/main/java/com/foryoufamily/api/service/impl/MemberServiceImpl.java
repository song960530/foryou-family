package com.foryoufamily.api.service.impl;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.repository.MemberRepository;
import com.foryoufamily.api.service.MemberService;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long join(JoinReqDto joinDto) {
        if (memberRepository.existsByMemberId(joinDto.getMemberId()))
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);

        return memberRepository.save(joinDto.toEntity()).getNo();
    }
}
