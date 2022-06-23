package com.foryoufamily.api.service.impl;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.entity.Member;
import com.foryoufamily.api.repository.MemberRepository;
import com.foryoufamily.api.service.MemberService;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 성공")
    public void successJoin() throws Exception {
        // given
        JoinReqDto joinDto = createJoinDto();
        Member member = createMember(joinDto);

        doReturn(false).when(memberRepository).existsByMemberId(anyString());
        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        Long saveMemberNo = memberService.join(joinDto);

        // then
        assertEquals(member.getNo(), saveMemberNo);
    }

    @Test
    @DisplayName("존재하는 아이디일때 오류 발생")
    public void duplicateMemberId() throws Exception {
        // given
        JoinReqDto joinDto = createJoinDto();

        doReturn(true).when(memberRepository).existsByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.join(joinDto);
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, customException.getErrorCode().getHttpStatus());
    }

    private Member createMember(JoinReqDto joinDto) {
        Member member = joinDto.toEntity();
        Long fakeId = 1L;
        ReflectionTestUtils.setField(member, "no", fakeId);

        return member;
    }

    private JoinReqDto createJoinDto() {
        return JoinReqDto.builder()
                .memberId("test123")
                .password("password123!@3")
                .build();
    }
}