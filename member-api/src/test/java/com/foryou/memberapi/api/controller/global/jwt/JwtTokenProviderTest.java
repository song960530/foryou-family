package com.foryou.memberapi.api.controller.global.jwt;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.entity.Member;
import com.foryou.memberapi.api.entity.Role;
import com.foryou.memberapi.api.enums.MemberRole;
import com.foryou.memberapi.global.jwt.JwtTokenProvider;
import com.foryou.memberapi.global.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}