package com.foryou.billingapi.global.iamport;

import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.properties.IamPortProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IamPortProviderTest {

    private IamPortProvider iamPortProvider;
    @Spy
    private IamPortProperties properties;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(properties, "apiKey", "9126903185453032");
        ReflectionTestUtils.setField(properties, "apiSecret", "21846cf8f313caf697b8909fc177d46f879c92c94439585bb9156172884c36bfd66caa96cf5329ff");
        iamPortProvider = new IamPortProvider(properties);
        iamPortProvider.init();
    }

    @Test
    @DisplayName("IamPortClient Bean 생성 확인")
    public void checkIamPortClientBean() throws Exception {
        // given

        // when

        // then
        assertNotNull(iamPortProvider);
    }

    @Test
    @DisplayName("accessToken 정상 생성")
    public void successCreateAccessToken() throws Exception {
        // given

        // when
        String accessToken = iamPortProvider.createAccessToken();

        // then
        assertNotNull(accessToken);
    }

    @Test
    @DisplayName("apikey와 apiSecret가 유효하지 않을 경우 오류 발생")
    public void notValidkey() throws Exception {
        // given
        ReflectionTestUtils.setField(properties, "apiKey", "1111111111111111");
        ReflectionTestUtils.setField(properties, "apiSecret", "11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        iamPortProvider.init();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            iamPortProvider.createAccessToken();
        });

        // then
        assertNotNull(customException);
        assertEquals(ErrorCode.NOT_VALID_IAMPORT_KEY, customException.getErrorCode());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getErrorCode().getHttpStatus());
    }
}