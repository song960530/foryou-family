package com.foryou.memberapi.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.repository.MemberRepository;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RestTemplate restTemplate;
    @Spy
    private ObjectMapper objectMapper;


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
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(Optional.empty()).when(memberRepository).findByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.login(loginReqDto, httpServletResponse);
        });

        // then
        assertEquals(ErrorCode.NOT_EXIST_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
    }


    @Test
    @DisplayName("아이디 조회는 되었으나 패스워드가 맞지 않을 때 오류 발생")
    public void notMatchedPassword() throws Exception {
        // given
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(Optional.of(member)).when(memberRepository).findByMemberId(anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            memberService.login(loginReqDto, httpServletResponse);
        });

        // then
        assertEquals(ErrorCode.NOT_MATCHED_PASSWORD, customException.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("로그인 성공_토큰정보가 없을 때")
    public void successLogin() throws Exception {
        // given
        String responseStr = "{\"status\":200,\"data\":{\"accessToken\":\"test\",\"refreshToken\":\"test\",\"type\":\"BEARER\"}}";
        ResponseEntity<String> apiResponse = new ResponseEntity<>(responseStr, HttpStatus.OK);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(apiResponse).when(restTemplate).postForEntity(anyString(), any(), any());
        doReturn(Optional.of(member)).when(memberRepository).findByMemberId(anyString());
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());

        // when
        LoginResDto result = memberService.login(loginReqDto, httpServletResponse);

        // then
        assertNotNull(result.getAccessToken());
        assertNotNull(result.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패_인증 API와 통신 오류")
    public void failLogin() throws Exception {
        // given
        String responseStr = "{\"status\":500,\"data\":{\"accessToken\":\"test\",\"refreshToken\":\"test\",\"type\":\"BEARER\"}}";
        ResponseEntity<String> apiResponse = new ResponseEntity<>(responseStr, HttpStatus.INTERNAL_SERVER_ERROR);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

        doReturn(apiResponse).when(restTemplate).postForEntity(anyString(), any(), any());
        doReturn(Optional.of(member)).when(memberRepository).findByMemberId(anyString());
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> memberService.login(loginReqDto, httpServletResponse));

        // then
        assertEquals(ErrorCode.LOGIN_FAIL_ERROR, customException.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getErrorCode().getHttpStatus());
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
