package com.foryoufamily.global.jwt;

import com.foryoufamily.api.entity.Role;
import com.foryoufamily.api.enums.MemberRole;
import com.foryoufamily.global.properties.JwtProperties;
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProperties, "secretKey", "01234567890123456789012345678912");
        ReflectionTestUtils.setField(jwtProperties, "validTime", 30L);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("프로퍼티 로딩 테스트")
    public void testProperties() throws Exception {
        // given
        String secretKey = jwtProperties.getSecretKey();
        long validTime = jwtProperties.getValidTime();

        // when

        // then
        assertEquals("01234567890123456789012345678912", secretKey);
        assertEquals(30L * 60_000, validTime);
    }

    @Test
    @DisplayName("JWT 정상 생성")
    public void successCreateJwt() throws Exception {
        // given
        String memberId = "test1234";
        List<Role> roles = Arrays.stream(MemberRole.values()).map(Role::new).collect(Collectors.toList());

        // when
        String token = jwtTokenProvider.createAccessToken(memberId, roles);

        // then
        assertNotNull(token);
    }
}