package com.foryou.authapi.global.jwt;


import com.foryou.authapi.global.error.CustomException;
import com.foryou.authapi.global.error.ErrorCode;
import com.foryou.authapi.global.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;
    @Spy
    private JwtProperties jwtProperties;

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

        // when
        String token = jwtTokenProvider.createAccessToken(memberId);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Refresh 토큰 정상 생성")
    public void successCreateRefresh() throws Exception {
        // given

        // when
        String token = jwtTokenProvider.createRefreshToken();

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("subject 추출 성공")
    public void successExtractSubject() throws Exception {
        // given
        String memberId = "test1234";
        String token = jwtTokenProvider.createAccessToken(memberId);

        // when
        String subject = jwtTokenProvider.extractSubject(token);

        // then
        assertEquals(memberId, subject);
    }

    @Test
    @DisplayName("subject 추출 중 오류 발생")
    public void exceptionExtractSubject() throws Exception {
        // given

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            jwtTokenProvider.extractSubject("test");
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_TOKEN_VALUE, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("subject 추출 중 만료된 토큰예외 처리")
    public void expiredToken() throws Exception {
        // given
        ReflectionTestUtils.setField(jwtProperties, "refreshValidTime", 0);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            jwtTokenProvider.extractSubject(refreshToken);
        });

        // then
        assertEquals(ErrorCode.EXPIRED_TOKEN, customException.getErrorCode());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("토큰 패턴 확인")
    public void matchedTokenPattern() throws Exception {
        // given
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // when
        boolean result = jwtTokenProvider.isMatchedPrefix("Bearer " + refreshToken);

        // then
        assertTrue(result);
    }
}