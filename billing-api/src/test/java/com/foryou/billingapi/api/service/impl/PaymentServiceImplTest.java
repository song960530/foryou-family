package com.foryou.billingapi.api.service.impl;

import com.foryou.billingapi.api.dto.request.CreatePaymentDto;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.dto.response.CardListResDto;
import com.foryou.billingapi.api.entity.PaymentHistory;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.entity.Product;
import com.foryou.billingapi.api.enums.OttType;
import com.foryou.billingapi.api.repository.PaymentRepository;
import com.foryou.billingapi.global.constants.Constants;
import com.foryou.billingapi.global.crypto.AES256Util;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.iamport.IamPortProvider;
import com.foryou.billingapi.global.properties.AES256Properties;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.AgainPaymentData;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private PaymentServiceImpl service;
    @Mock
    IamPortProvider iamPortProvider;
    @Spy
    PaymentRepository repository;
    @Mock
    OnetimePaymentData onetimePaymentData;
    AES256Util aes256Util;
    AES256Properties aes256Properties;

    Payment payment;
    IamportResponse<Payment> iamportResponse;

    @BeforeEach
    void setUp() {
        aes256Properties = new AES256Properties();
        ReflectionTestUtils.setField(aes256Properties, "key", "abcdefghijklmnopabcdefghijklmnop");
        ReflectionTestUtils.setField(aes256Properties, "iv", "abcdefghijklmnop");
        aes256Util = new AES256Util(aes256Properties);
        aes256Util.init();
        service = new PaymentServiceImpl(iamPortProvider, repository, aes256Util);
        payment = createIamPortPayment();
        iamportResponse = createResponse(payment);
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
        doReturn(false).when(iamPortProvider).checkResponse(any(IamportResponse.class));

        // when
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> {
            service.doFirstPay(onetimePaymentData);
        });

        // then
        assertEquals(ErrorCode.CARD_REGISTRATION_FAILED, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("카드 검증 시 100원이 아닌 다른 금액이 결제되었을 경우 오류 발생")
    public void notMatchedExpectedAmount() throws Exception {
        // given
        doReturn(iamportResponse).when(iamPortProvider).pay(onetimePaymentData);
        doReturn(true).when(iamPortProvider).checkResponse(any(IamportResponse.class));
        doReturn(false).when(iamPortProvider).validAmount(any(BigDecimal.class), any(BigDecimal.class));

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
        ReflectionTestUtils.setField(iamportResponse.getResponse(), "amount", BigDecimal.valueOf(100L));
        ReflectionTestUtils.setField(iamportResponse.getResponse(), "customer_uid", customerUid);
        doReturn(iamportResponse).when(iamPortProvider).pay(onetimePaymentData);
        doReturn(true).when(iamPortProvider).checkResponse(any(IamportResponse.class));
        doReturn(true).when(iamPortProvider).validAmount(any(BigDecimal.class), any(BigDecimal.class));

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

    @Test
    @DisplayName("payment 엔티티가 null 일 경우 paymentHistory 엔티티만 연결")
    public void connectPaymentHistoryWhenPaymentIsNull() throws Exception {
        // given
        Product product = Product.builder().build();
        PaymentHistory paymentHistory = PaymentHistory.builder().build();

        // when
        service.recordProduct(null, product, paymentHistory);

        // then
        assertEquals(paymentHistory, product.getPaymentHistories().get(0));
        assertEquals(product, paymentHistory.getProduct());
        assertNull(product.getPayment());
    }

    @Test
    @DisplayName("Payment엔티티가 null이 아닐 경우 전체 연결")
    public void connectAll() throws Exception {
        // given
        Payments payment = Payments.builder().build();
        Product product = Product.builder().build();
        PaymentHistory paymentHistory = PaymentHistory.builder().build();

        // when
        service.recordProduct(payment, product, paymentHistory);

        // then
        assertEquals(product, payment.getProducts().get(0));
        assertEquals(paymentHistory, product.getPaymentHistories().get(0));
        assertEquals(product, paymentHistory.getProduct());
    }

    @Test
    @DisplayName("CustomerUid로 결제 진행 중 통신 오류 발생")
    public void ConnectionErrorWhenPayAgain() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();
        Payments payment = createPayment();

        doReturn(Optional.of(payment)).when(repository).findById(anyLong());
        doThrow(IamportResponseException.class).when(iamPortProvider).payAgain(any(AgainPaymentData.class));

        // when
        boolean result = service.doPayAgain(request);

        // then
        assertEquals(false, result);
    }

    @Test
    @DisplayName("결제카드 정보를 찾지 못하였을 경우 오류 발생")
    public void notFoundPaymentError() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();

        doReturn(Optional.empty()).when(repository).findById(anyLong());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> service.doPayAgain(request));

        // then
        assertEquals(ErrorCode.NOT_FOUND_PAYMENT, customException.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("카드 등록자와 결제 요청자 정보가 다를경우 오류 발생")
    public void notMatchMemberIdError() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();
        request.setMemberId("notMatchMemberId");
        Payments payment = createPayment();

        doReturn(Optional.of(payment)).when(repository).findById(anyLong());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> service.doPayAgain(request));

        // then
        assertEquals(ErrorCode.NOT_MATCH_MEMBERID, customException.getErrorCode());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("CustomerUid로 결제 중 카드 이상 발생")
    public void failResponseCheckWhenPayAgain() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();
        Payments payment = createPayment();

        doReturn(Optional.of(payment)).when(repository).findById(anyLong());
        doReturn(iamportResponse).when(iamPortProvider).payAgain(any(AgainPaymentData.class));
        doReturn(false).when(iamPortProvider).checkResponse(any(IamportResponse.class));

        // when
        boolean result = service.doPayAgain(request);

        // then
        assertEquals(false, result);
    }

    @Test
    @DisplayName("CustomerUid를 사용하여 정상 결제 완료")
    public void successPayUseCusutomerUid() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();
        Payments payment = createPayment();

        doReturn(Optional.of(payment)).when(repository).findById(anyLong());
        doReturn(iamportResponse).when(iamPortProvider).payAgain(any(AgainPaymentData.class));
        doReturn(true).when(iamPortProvider).checkResponse(any(IamportResponse.class));
        doReturn(true).when(iamPortProvider).validAmount(any(BigDecimal.class), any(BigDecimal.class));

        // when
        boolean result = service.doPayAgain(request);

        // then
        assertEquals(true, result);
    }

    @Test
    @DisplayName("CustomerUid를 사용하여 결제가 이뤄졌으나 예상 결제금액이 맞지 않을 경우")
    public void cancelAfterPay() throws Exception {
        // given
        PaymentRequestMessage request = createPaymentRequestMessage();
        Payments payment = createPayment();

        doReturn(Optional.of(payment)).when(repository).findById(anyLong());
        doReturn(iamportResponse).when(iamPortProvider).payAgain(any(AgainPaymentData.class));
        doReturn(true).when(iamPortProvider).checkResponse(any(IamportResponse.class));
        doReturn(false).when(iamPortProvider).validAmount(any(BigDecimal.class), any(BigDecimal.class));
        doReturn(createResponse(createIamPortPayment())).when(iamPortProvider).cancelPay(anyString(), any(BigDecimal.class), anyString());

        // when
        boolean result = service.doPayAgain(request);

        // then
        assertEquals(false, result);
    }

    @Test
    @DisplayName("결제카드리스트 정상생성")
    public void successCardList() throws Exception {
        // given
        String memberId = "test123";

        Payments payment1 = Payments.builder()
                .memberId(memberId)
                .cardNum4Digit(aes256Util.encrypt("1234-1234-1234-1234".split("-")[3]))
                .build();
        Payments payment2 = Payments.builder()
                .memberId(memberId)
                .cardNum4Digit(aes256Util.encrypt("1234-1234-1234-4321".split("-")[3]))
                .build();

        doReturn(List.of(payment1, payment2)).when(repository).usePaymentList(memberId);

        // when
        CardListResDto result = service.myPaymentCardList(memberId);

        // then
        assertEquals(2, result.getCount());
        assertEquals(memberId, result.getMemberId());
        assertEquals(aes256Util.encrypt("1234"), result.getPaymentCardList().get(0).getCardNum4Digit());
        assertEquals(aes256Util.encrypt("4321"), result.getPaymentCardList().get(1).getCardNum4Digit());
    }

    private IamportResponse<Payment> createResponse(Payment payment) {
        IamportResponse<Payment> response = new IamportResponse<>();
        ReflectionTestUtils.setField(response, "message", "카드이상 오류 발생");
        ReflectionTestUtils.setField(response, "response", payment);

        return response;
    }

    private Payment createIamPortPayment() {
        Payment payment = new Payment();
        ReflectionTestUtils.setField(payment, "amount", BigDecimal.valueOf(5000L));
        ReflectionTestUtils.setField(payment, "merchant_uid", "test_merchant_uid");

        return payment;
    }

    private Payments createPayment() {
        String memberId = "testMember";
        String customerUid = "testCustomerUid";

        return Payments.builder()
                .customerUid(customerUid)
                .memberId(memberId)
                .build();
    }

    private PaymentRequestMessage createPaymentRequestMessage() {
        String memberId = "testMember";
        Long fakePartyNo = 1L;
        Long fakePaymentNo = 1L;
        OttType ott = OttType.NETFLIX;

        return PaymentRequestMessage.builder()
                .memberId(memberId)
                .paymentNo(fakePaymentNo)
                .partyNo(fakePartyNo)
                .ott(ott)
                .build();
    }
}