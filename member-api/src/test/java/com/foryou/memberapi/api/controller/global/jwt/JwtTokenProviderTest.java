package com.foryou.memberapi.api.controller.global.jwt;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.entity.Role;
import com.foryou.memberapi.api.enums.MemberRole;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import com.foryou.memberapi.global.jwt.JwtTokenProvider;
import com.foryou.memberapi.global.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;
    @Spy
    private JwtProperties jwtProperties;

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
    @DisplayName("subject 추출 성공")
    public void successExtractSubject() throws Exception {
        // given
        String memberId = "test1234";
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        // when
        String subject = jwtTokenProvider.extractSubject(refreshToken);

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
        String memberId = "test1234";
        ReflectionTestUtils.setField(jwtProperties, "refreshValidTime", 0);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

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
        String memberId = "test1234";
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        // when
        boolean result = jwtTokenProvider.isMatchedPrefix("Bearer " + refreshToken);

        // then
        assertTrue(result);
    }
}