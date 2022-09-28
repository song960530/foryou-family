package com.foryou.billingapi.global.iamport;

import com.foryou.billingapi.global.constants.Constants;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.properties.IamPortProperties;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IamPortProviderTest {

    private IamPortProvider iamPortProvider;
    @Spy
    private IamPortProperties properties;

    private OnetimePaymentData successDto;
    private OnetimePaymentData cardNumFailDto;
    private OnetimePaymentData otherFailDto;

    @BeforeEach
    void setUp() {
        successDto = createSuccessOnetimePaymentData();
        cardNumFailDto = createFailOnetimePaymentData1();
        otherFailDto = createFailOnetimePaymentData2();

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

    @Test
    @DisplayName("정상결제")
    public void successPay() throws Exception {
        // given

        // when
        IamportResponse<Payment> response = iamPortProvider.pay(successDto);

        // then
        assertEquals(0, response.getCode());
        assertEquals("paid", response.getResponse().getStatus());
    }

    @Test
    @DisplayName("apikey와 apiSecret가 유효하지 않을 경우 오류 발생")
    public void notValidkeyInPay() throws Exception {
        // given
        ReflectionTestUtils.setField(properties, "apiKey", "1111111111111111");
        ReflectionTestUtils.setField(properties, "apiSecret", "11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        iamPortProvider.init();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            iamPortProvider.pay(successDto);
        });

        // then
        assertNotNull(customException);
        assertEquals(ErrorCode.NOT_VALID_IAMPORT_KEY, customException.getErrorCode());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("카드정보에 문제가 있어 결제가 정상처리되지 않았을 경우")
    public void failPayCauseCard() throws Exception {
        // given

        // when
        IamportResponse<Payment> response = iamPortProvider.pay(cardNumFailDto);

        // then
        assertEquals(-1, response.getCode());
        assertNull(response.getResponse());
        assertNotNull(response.getMessage());
    }

    @Test
    @DisplayName("카드는 문제가 없으나 결제 중 다른 오류가 발생했을 경우")
    public void failPayOther() throws Exception {
        // given

        // when
        IamportResponse<Payment> response = iamPortProvider.pay(otherFailDto);

        // then
        assertEquals(0, response.getCode());
        assertNull(response.getMessage());
        assertNotNull(response.getResponse());
        assertNotEquals("paid", response.getResponse().getStatus());
        assertNotNull(response.getResponse().getFailReason());
    }

    @Test
    @DisplayName("결제 이상없이 정상결제 확인")
    public void successCheckResponse() throws Exception {
        // given
        IamportResponse<Payment> response = iamPortProvider.pay(successDto);

        // when
        boolean check = iamPortProvider.checkResponse(response);

        // then
        assertEquals(true, check);
    }

    @Test
    @DisplayName("카드 정보가 잘봇되어서 응답code가 0 이 아닐경우")
    public void checkResultFailCauseCard() throws Exception {
        // given
        IamportResponse<Payment> response = iamPortProvider.pay(cardNumFailDto);

        // when
        boolean check = iamPortProvider.checkResponse(response);

        // then
        assertEquals(false, check);
    }

    @Test
    @DisplayName("카드정보는 맞으나 결제정보가 잘못되었을 경우")
    public void checkRdsultFailCausePayInfo() throws Exception {
        // given
        IamportResponse<Payment> response = iamPortProvider.pay(otherFailDto);

        // when
        boolean check = iamPortProvider.checkResponse(response);

        // then
        assertEquals(false, check);
    }

    @Test
    @DisplayName("결제된 금액이 예상 결제금액과 맞을 경우")
    public void successCheckAmount() throws Exception {
        // given
        IamportResponse<Payment> response = iamPortProvider.pay(successDto);

        // when
        boolean checkAmount = iamPortProvider.validAmount(BigDecimal.valueOf(100), response.getResponse().getAmount());

        // then
        assertEquals(true, checkAmount);
    }

    @Test
    @DisplayName("결제된 금액이 예상 결제금액과 맞지 않을 경우")
    public void failCheckAmount() throws Exception {
        // given
        IamportResponse<Payment> response = iamPortProvider.pay(successDto);

        // when
        boolean checkAmount = iamPortProvider.validAmount(BigDecimal.valueOf(1000), response.getResponse().getAmount());

        // then
        assertEquals(false, checkAmount);
    }

    @Test
    @DisplayName("결제 정상 취소")
    public void successCancelPay() throws Exception {
        // given
        IamportResponse<Payment> payResponse = iamPortProvider.pay(successDto);

        // when
        IamportResponse<Payment> cancelResponse = iamPortProvider.cancelPay(
                payResponse.getResponse().getMerchantUid()
                , payResponse.getResponse().getAmount()
                , "결제 취소"
        );

        // then
        assertEquals(0, cancelResponse.getCode());
        assertEquals("cancelled", cancelResponse.getResponse().getStatus());
        assertNull(cancelResponse.getMessage());
    }

    @Test
    @DisplayName("취소 가능 금액을 초과하여 결제 취소 실패")
    public void failCancelPay() throws Exception {
        // given
        IamportResponse<Payment> payResponse = iamPortProvider.pay(successDto);

        // when
        IamportResponse<Payment> cancelResponse = iamPortProvider.cancelPay(
                payResponse.getResponse().getMerchantUid()
                , BigDecimal.valueOf(1000)
                , "결제 취소"
        );

        // then
        assertNotEquals(0, cancelResponse.getCode());
        assertNotNull(cancelResponse.getMessage());
        assertNull(cancelResponse.getResponse());
    }

    @Test
    @DisplayName("취소 가능 금액을 초과하여 결제 취소 실패")
    public void failCancelPayCauseAuth() throws Exception {
        // given
        IamportResponse<Payment> payResponse = iamPortProvider.pay(successDto);
        ReflectionTestUtils.setField(properties, "apiKey", "1111111111111111");
        ReflectionTestUtils.setField(properties, "apiSecret", "11111111111111111111111111111111111111111111111111111111111111111111111111111111");
        iamPortProvider.init();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            iamPortProvider.cancelPay(
                    payResponse.getResponse().getMerchantUid()
                    , payResponse.getResponse().getAmount()
                    , "결제 취소"
            );
        });

        // then
        assertEquals(ErrorCode.NOT_VALID_IAMPORT_KEY, customException.getErrorCode());
        assertEquals(ErrorCode.NOT_VALID_IAMPORT_KEY.getHttpStatus(), HttpStatus.UNAUTHORIZED);
    }

    public OnetimePaymentData createSuccessOnetimePaymentData() {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;
        String customerUid = "test123" + Constants.UNDER_BAR + milliSeconds;
        String pgStoreId = Constants.PG_TYPE_KCP + Constants.COMMA + Constants.KCP_STORE_ID;

        CardInfo cardInfo = new CardInfo(
                "5137-9200-4241-9667"
                , "2024-12"
                , "960530"
                , "00"
        );

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, BigDecimal.valueOf(100), cardInfo);
        onetimePaymentData.setPg(pgStoreId);
        onetimePaymentData.setName("테스트 결제");
        onetimePaymentData.setBuyerName("test123");
        onetimePaymentData.setCustomer_uid(customerUid);

        return onetimePaymentData;
    }

    public OnetimePaymentData createFailOnetimePaymentData1() {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;
        String customerUid = "test123" + Constants.UNDER_BAR + milliSeconds;
        String pgStoreId = Constants.PG_TYPE_KCP + Constants.COMMA + Constants.KCP_STORE_ID;

        CardInfo cardInfo = new CardInfo(
                "1234-1234-1234-1234"
                , "2024-12"
                , "960530"
                , "00"
        );

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, BigDecimal.valueOf(100), cardInfo);
        onetimePaymentData.setPg(pgStoreId);
        onetimePaymentData.setName("테스트 결제");
        onetimePaymentData.setBuyerName("test123");
        onetimePaymentData.setCustomer_uid(customerUid);

        return onetimePaymentData;
    }

    public OnetimePaymentData createFailOnetimePaymentData2() {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;
        String customerUid = "test123" + Constants.UNDER_BAR + milliSeconds;
        String pgStoreId = Constants.PG_TYPE_KCP + Constants.COMMA + Constants.KCP_STORE_ID;

        CardInfo cardInfo = new CardInfo(
                "5137-9200-4241-9667"
                , "2024-12"
                , "960530"
                , "00"
        );

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, BigDecimal.valueOf(1), cardInfo);
        onetimePaymentData.setPg(pgStoreId);
        onetimePaymentData.setName("테스트 결제");
        onetimePaymentData.setBuyerName("test123");
        onetimePaymentData.setCustomer_uid(customerUid);

        return onetimePaymentData;
    }
}