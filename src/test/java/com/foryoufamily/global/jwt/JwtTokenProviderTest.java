package com.foryoufamily.global.jwt;

import com.foryoufamily.api.entity.Role;
import com.foryoufamily.api.enums.MemberRole;
import com.foryoufamily.global.error.CustomException;
import com.foryoufamily.global.error.ErrorCode;
import com.foryoufamily.global.properties.JwtProperties;
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Spy
    private JwtProperties jwtProperties;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProperties, "secretKey", "01234567890123456789012345678912");
        ReflectionTestUtils.setField(jwtProperties, "accessValidTime", 30);
        ReflectionTestUtils.setField(jwtProperties, "refreshValidTime", 1);
        jwtTokenProvider.init();
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
        String token = jwtTokenProvider.resolveToken(request);

        // then
        Assertions.assertEquals("", token);
    }

    @Test
    @DisplayName("ServletRequest Authorization헤더값에 토큰이 있으면 토큰만 리턴")
    public void authorizationValueIsNotNull() throws Exception {
        // given
        String authorizationHeaderValue = "Bearer This is Test Token";

        doReturn(authorizationHeaderValue).when(request).getHeader("Authorization");

        // when
        String resolveToken = jwtTokenProvider.resolveToken(request);

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
            jwtTokenProvider.resolveToken(request);
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_TOKEN_FORM, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }
}