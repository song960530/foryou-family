package com.foryoufamily.api.service.impl;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.dto.request.LoginReqDto;
import com.foryoufamily.api.dto.response.LoginResDto;
import com.foryoufamily.api.entity.Member;
import com.foryoufamily.api.repository.MemberRepository;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import com.foryoufamily.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private LoginReqDto loginReqDto;
    private JoinReqDto joinReqDto;
    private Member member;

    @BeforeEach
    void setUp() {
        joinReqDto = createJoinDto();
        member = createMember(joinReqDto);
        loginReqDto = getLoginReqDto();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void successJoin() throws Exception {
        // given
        doReturn(false).when(memberRepository).existsByMemberId(anyString());
        doReturn(member).when(memberRepository).save(any(Member.class));

        // when
        Long saveMemberNo = memberService.join(joinReqDto);

        // then
        assertEquals(member.getNo(), saveMemberNo);
    }

    @Test
    @DisplayName("존재하는 아이디일때 오류 발생")
    public void duplicateMemberId() throws Exception {
        // given
        doReturn(true).when(memberRepository).existsByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.join(joinReqDto);
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("아이디를 조회하지 못했을 때 오류 발생")
    public void notExistMemberId() throws Exception {
        // given
        doReturn(Optional.empty()).when(memberRepository).findByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.login(loginReqDto);
        });

        // then
        assertEquals(ErrorCode.NOT_EXIST_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
    }


    @Test
    @DisplayName("아이디 조회는 되었으나 패스워드가 맞지 않을 때 오류 발생")
    public void notMatchedPassword() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.login(loginReqDto);
        });

        // then
        assertEquals(ErrorCode.NOT_MATCHED_PASSWORD, customException.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("로그인 성공")
    public void successLogin() throws Exception {
        // given
        doReturn(Optional.of(member)).when(memberRepository).findByMemberId(anyString());
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());
        doReturn("test").when(jwtTokenProvider).createAccessToken(anyString(), anyList());
        doReturn("test").when(jwtTokenProvider).createRefreshToken(anyString());

        // when
        LoginResDto result = memberService.login(loginReqDto);

        // then
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
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

    private LoginReqDto getLoginReqDto() {
        return LoginReqDto.builder()
                .memberId("test123")
                .password("password123!@3")
                .build();
    }
}
