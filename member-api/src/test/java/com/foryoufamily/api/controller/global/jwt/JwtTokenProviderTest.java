package com.foryoufamily.api.controller.global.jwt;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.entity.Member;
import com.foryoufamily.api.entity.Role;
import com.foryoufamily.api.enums.MemberRole;
import com.foryoufamily.global.constants.Constants;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import com.foryoufamily.global.jwt.JwtTokenProvider;
import com.foryoufamily.global.properties.JwtProperties;
import com.foryoufamily.global.security.UserAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private JwtProperties jwtProperties;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember(createJoinDto());
        ReflectionTestUtils.setField(jwtProperties, "secretKey", "01234567890123456789012345678912");
        ReflectionTestUtils.setField(jwtProperties, "accessValidTime", 30);
        ReflectionTestUtils.setField(jwtProperties, "refreshValidTime", 1);
        jwtTokenProvider.init();
    }

    private JoinReqDto createJoinDto() {
        return JoinReqDto.builder()
                .memberId("test123")
                .password("password123!@3")
                .build();
    }

    private Member createMember(JoinReqDto joinDto) {
        Member member = joinDto.toEntity();
        Long fakeId = 1L;
        ReflectionTestUtils.setField(member, "no", fakeId);

        return member;
    }

    @Test
    @DisplayName("프로퍼티 로딩 테스트")
    public void testProperties() throws Exception {
        // given
        String secretKey = jwtProperties.getSecretKey();
        long validTime = jwtProperties.getAccessValidTime();

        // when

        // then
        assertEquals("01234567890123456789012345678912", secretKey);
        assertEquals(30L * 60_000, validTime);
    }

    @Test
    @DisplayName("Access 토큰 정상 생성")
    public void successCreateAccess() throws Exception {
        // given
        String memberId = "test1234";
        List<Role> roles = Arrays.stream(MemberRole.values()).map(Role::new).collect(Collectors.toList());

        // when
        String token = jwtTokenProvider.createAccessToken(memberId, roles);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Refresh 토큰 정상 생성")
    public void successCreateRefresh() throws Exception {
        // given
        String memberId = "test1234";

        // when
        String token = jwtTokenProvider.createRefreshToken(memberId);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("ServletRequest Authorization헤더값이 null일때 빈문자열 리턴")
    public void authorizationValueIsNull() throws Exception {
        // given
        doReturn(null).when(request).getHeader("Authorization");

        // when
        String token = jwtTokenProvider.extractToken(request);

        // then
        Assertions.assertEquals(Constants.DEFAULT_TOKEN_VALUE, token);
    }

    @Test
    @DisplayName("ServletRequest Authorization헤더값에 토큰이 있으면 토큰만 리턴")
    public void authorizationValueIsNotNull() throws Exception {
        // given
        String authorizationHeaderValue = "Bearer This is Test Token";

        doReturn(authorizationHeaderValue).when(request).getHeader("Authorization");

        // when
        String resolveToken = jwtTokenProvider.extractToken(request);

        // then
        assertEquals(authorizationHeaderValue.replaceAll("^(?i)Bearer( )*", ""), resolveToken);
    }

    @Test
    @DisplayName("Authorization헤더값 형태가 옳지 않을경우 오류 발생")
    public void notValidHeaderForm() throws Exception {
        // given
        String authorizationHeaderValue = "Bearer123 This is Test Token";

        doReturn(authorizationHeaderValue).when(request).getHeader("Authorization");

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            jwtTokenProvider.extractToken(request);
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_TOKEN_FORM, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("JWT 토큰을 파싱하여 Subject를 추출한다")
    public void passTokenExpire() throws Exception {
        // given
        String memberId = "test1234";
        List<Role> roles = Arrays.stream(MemberRole.values()).map(Role::new).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.createAccessToken(memberId, roles);

        // when
        String reuslt = jwtTokenProvider.extractSubject(accessToken);

        // then
        assertEquals("test1234", reuslt);
    }

    @Test
    @DisplayName("토큰 만료일이 지나서 ErrorCode.EXPIRED_TOKEN 발생")
    public void expiredToken() throws Exception {
        // given
        String memberId = "test1234";
        List<Role> roles = Arrays.stream(MemberRole.values()).map(Role::new).collect(Collectors.toList());
        ReflectionTestUtils.setField(jwtProperties, "accessValidTime", 0);
        String accessToken = jwtTokenProvider.createAccessToken(memberId, roles);

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            jwtTokenProvider.extractSubject(accessToken);
        });

        // then
        assertEquals(ErrorCode.EXPIRED_TOKEN, customException.getErrorCode());
        assertEquals(ErrorCode.EXPIRED_TOKEN.getHttpStatus(), customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("Header로 넘어온 토큰의 Form은 맞으나 내부 값 검증이 실패했을 때 오류 발생")
    public void failValidTokenValue() throws Exception {
        // given
        String failToken = "header.payload.sign";

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            jwtTokenProvider.extractSubject(failToken);
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_TOKEN_VALUE, customException.getErrorCode());
        assertEquals(ErrorCode.NOT_VALID_TOKEN_VALUE.getHttpStatus(), customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("Authentication 객체 생성 성공")
    public void successCreateAuthentication() throws Exception {
        // given
        UserAdapter userAdapter = new UserAdapter(member);

        doReturn(userAdapter).when(userDetailsService).loadUserByUsername(anyString());

        // when
        Authentication result = jwtTokenProvider.createAuthentication("test123");

        // then
        assertTrue(result.getPrincipal().equals(userAdapter));
    }
}