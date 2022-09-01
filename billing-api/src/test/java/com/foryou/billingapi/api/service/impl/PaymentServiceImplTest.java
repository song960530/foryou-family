package com.foryou.billingapi.api.service.impl;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.repository.PaymentRepository;
import com.foryou.billingapi.global.Constants;
import com.foryou.billingapi.global.crypto.AES256Util;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.iamport.IamPortProvider;
import com.foryou.billingapi.global.properties.AES256Properties;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private PaymentServiceImpl service;
    @Mock
    IamPortProvider iamPortProvider;
    @Spy
    PaymentRepository repository;
    @Mock
    IamportResponse<Payment> iamportResponse;
    @Mock
    Payment payment;
    @Mock
    OnetimePaymentData onetimePaymentData;
    AES256Util aes256Util;
    AES256Properties aes256Properties;

    @BeforeEach
    void setUp() {
        aes256Properties = new AES256Properties();
        ReflectionTestUtils.setField(aes256Properties, "key", "abcdefghijklmnopabcdefghijklmnop");
        ReflectionTestUtils.setField(aes256Properties, "iv", "abcdefghijklmnop");
        aes256Util = new AES256Util(aes256Properties);
        aes256Util.init();
        service = new PaymentServiceImpl(iamPortProvider, repository, aes256Util);
    }

    @Test
    @DisplayName("OnetimePaymentData 클래스 생성 확인")
    public void createOntimePaymentData() throws Exception {
        // given
        String memberId = "test123";
        CreatePaymentDto createPaymentDto = new CreatePaymentDto(
                aes256Util.encrypt("1234-1234-1234-1234")
                , aes256Util.encrypt("2024-12")
                , aes256Util.encrypt("960530")
                , aes256Util.encrypt("00"));

        // when
        OnetimePaymentData result = service.createOnetimePaymentData(memberId, createPaymentDto, Constants.CHECK_CARD, BigDecimal.valueOf(100));

        // then
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("카드 확인용 첫 결제 중 카드정보 이상으로 인하여 오류 발생")
    public void failFirstPayCauseCard() throws Exception {
        // given
        doReturn(iamportResponse).when(iamPortProvider).pay(onetimePaymentData);
        doReturn("").when(iamportResponse).getMessage();
        doReturn(false).when(iamPortProvider).checkResponse(any());

        // when
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
            service.doFirstPay(onetimePaymentData);
        });

        // then
        assertEquals(ErrorCode.CARD_REGISTRATION_FAILED, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("예상 결제금액과 맞지 않아 결제 취소 후 오류 발생")
    public void notMatchedExpectedAmount() throws Exception {
        // given
        doReturn(payment).when(iamportResponse).getResponse();
        doReturn(BigDecimal.valueOf(10000)).when(payment).getAmount();
        doReturn(iamportResponse).when(iamPortProvider).pay(onetimePaymentData);
        doReturn(true).when(iamPortProvider).checkResponse(any());
        doReturn(false).when(iamPortProvider).validAmount(any(), any());

        // when
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
            service.doFirstPay(onetimePaymentData);
        });

        // then
        assertEquals(ErrorCode.CARD_REGISTRATION_FAILED, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("카드 확인 성공")
    public void successFirstPay() throws Exception {
        // given
        String customerUid = "customerUid";

        doReturn(iamportResponse).when(iamPortProvider).pay(onetimePaymentData);
        doReturn(true).when(iamPortProvider).checkResponse(any());
        doReturn(payment).when(iamportResponse).getResponse();
        doReturn(BigDecimal.valueOf(10000)).when(payment).getAmount();
        doReturn(true).when(iamPortProvider).validAmount(any(), any());
        doReturn(customerUid).when(payment).getCustomerUid();

        // when
        String result = service.doFirstPay(onetimePaymentData);
        // then

        assertNotNull(result);
        assertEquals(customerUid, result);
    }

    @Test
    @DisplayName("결제카드 등록 완료")
    public void successRegistPayment() throws Exception {
        // given
        String memberId = "test123";
        String customerUid = "customerUid";
        String cardNum = aes256Util.encrypt("1234-1234-1234-1234");
        Payments fakePayment = Payments.builder()
                .memberId(memberId)
                .customerUid(customerUid)
                .cardNum4Digit(aes256Util.decrypt(cardNum).split("-")[3])
                .build();
        ReflectionTestUtils.setField(fakePayment, "no", 1L);

        doReturn(fakePayment).when(repository).save(any(Payments.class));

        // when
        Long no = service.registPayment(memberId, customerUid, cardNum);

        // then
        assertEquals(1L, no);
    }
}