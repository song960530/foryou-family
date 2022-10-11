package com.foryou.authapi.api.service.impl;

import com.foryou.authapi.api.dto.TokenResDto;
import com.foryou.authapi.api.entity.Token;
import com.foryou.authapi.api.repository.AuthRepository;
import com.foryou.authapi.global.constants.Constants;
import com.foryou.authapi.global.error.CustomException;
import com.foryou.authapi.global.error.ErrorCode;
import com.foryou.authapi.global.jwt.JwtTokenProvider;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    AuthServiceImpl service;
    @Mock
    private JwtTokenProvider provider;
    @Mock
    private AuthRepository repository;
    TestData testData;

    @BeforeEach
    void setUp() {
        testData = new TestData();
    }

    @Test
    @DisplayName("최초 로그인일 경우 Token 엔티티 생성")
    public void firstLogin() throws Exception {
        // given
        doReturn(testData.accessToken).when(provider).createAccessToken(anyString());
        doReturn(testData.refreshToken).when(provider).createRefreshToken(anyString());
        doReturn(Optional.empty()).when(repository).findByMemberId(anyString());
        doReturn(testData.token).when(repository).save(any(Token.class));

        // when
        TokenResDto result = service.createOrUpdateToken(testData.memberId);

        // then
        assertEquals(testData.accessToken, result.getAccessToken());
        assertEquals(testData.refreshToken, result.getRefreshToken());
        assertEquals(Constants.TOKEN_TYPE, result.getType());
    }

    @Test
    @DisplayName("기존 이력이 있을 경우 Access와 Refresh 토큰 Update")
    public void loginMoreThanOnce() throws Exception {
        // given
        String reCreateAcToken = "reCreateAccessToken";
        String reCreateRfToken = "reCreateRefreshToken";

        doReturn(reCreateAcToken).when(provider).createAccessToken(anyString());
        doReturn(reCreateRfToken).when(provider).createRefreshToken(anyString());
        doReturn(Optional.of(testData.token)).when(repository).findByMemberId(anyString());

        // when
        TokenResDto result = service.createOrUpdateToken(testData.memberId);

        // then
        assertEquals(reCreateAcToken, result.getAccessToken());
        assertEquals(reCreateRfToken, result.getRefreshToken());
        assertEquals(Constants.TOKEN_TYPE, result.getType());
    }

    @Test
    @DisplayName("쿠키에 아무 데이터도 존재하지 않을 경우 오류 발생")
    public void noDataInCookie() throws Exception {
        // given
        doReturn(null).when(testData.httpServletRequest).getCookies();

        // when
        CustomException customException = assertThrows(
                CustomException.class
                , () -> service.reCreateToken(testData.memberId, testData.httpServletRequest, testData.httpServletResponse)
        );

        // then
        assertEquals(ErrorCode.NOt_EXIST_REFRESH_TOKEN, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("쿠키에 데이터는 있지만 refreshToken정보가 없을 경우 오류 발생")
    public void noRefreshTokenInCookie() throws Exception {
        // given
        doReturn(Arrays.array(new Cookie("test", "test"))).when(testData.httpServletRequest).getCookies();

        // when
        CustomException customException = assertThrows(
                CustomException.class
                , () -> service.reCreateToken(testData.memberId, testData.httpServletRequest, testData.httpServletResponse)
        );

        // then
        assertEquals(ErrorCode.NOt_EXIST_REFRESH_TOKEN, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("요청 들어온 memberId와 refreshToken으로 발급된 내역이 없을 경우 오류 발생")
    public void noHistoryCreateToken() throws Exception {
        // given
        doReturn(Arrays.array(new Cookie(Constants.REFRESH_TOKEN_HEADER_NAME, testData.refreshToken))).when(testData.httpServletRequest).getCookies();
        doReturn(Optional.empty()).when(repository).findByMemberIdAndRefreshToken(testData.memberId, testData.refreshToken);

        // when
        CustomException customException = assertThrows(
                CustomException.class
                , () -> service.reCreateToken(testData.memberId, testData.httpServletRequest, testData.httpServletResponse)
        );

        // then
        assertEquals(ErrorCode.ARGUMENT_NOT_VALID, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("토큰 재발급 완료")
    public void successReCreateToken() throws Exception {
        // given
        String reCreateAcToken = "reCreateAccessToken";
        String reCreateRfToken = "reCreateRefreshToken";

        doReturn(Arrays.array(new Cookie(Constants.REFRESH_TOKEN_HEADER_NAME, testData.refreshToken))).when(testData.httpServletRequest).getCookies();
        doReturn(Optional.of(testData.token)).when(repository).findByMemberIdAndRefreshToken(testData.memberId, testData.refreshToken);
        doReturn(reCreateAcToken).when(provider).createAccessToken(anyString());
        doReturn(reCreateRfToken).when(provider).createRefreshToken(anyString());

        // when
        TokenResDto result = service.reCreateToken(testData.memberId, testData.httpServletRequest, testData.httpServletResponse);

        // then
        assertEquals(reCreateAcToken, result.getAccessToken());
        assertEquals("httponly", result.getRefreshToken());
        assertEquals(Constants.TOKEN_TYPE, result.getType());
    }
}

class TestData {
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";
    String memberId = "testMember";

    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

    Token token = Token.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .memberId(memberId)
            .build();
}