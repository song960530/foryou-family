package com.foryou.billingapi.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.billingapi.api.dto.request.CreatePaymentDto;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.entity.PaymentHistory;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.entity.Product;
import com.foryou.billingapi.api.enums.OttType;
import com.foryou.billingapi.api.enums.PaymentType;
import com.foryou.billingapi.api.repository.PaymentRepository;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.constants.Constants;
import com.foryou.billingapi.global.crypto.AES256Util;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.iamport.IamPortProvider;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.AgainPaymentData;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final IamPortProvider iamPortProvider;
    private final PaymentRepository paymentRepository;
    private final AES256Util aes256Util;

    @Override
    public OnetimePaymentData createOnetimePaymentData(String memberId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price) {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;
        String customerUid = memberId + Constants.UNDER_BAR + milliSeconds;
        String pgStoreId = Constants.PG_TYPE_KCP + Constants.COMMA + Constants.KCP_STORE_ID;

        CardInfo cardInfo = new CardInfo(
                aes256Util.decrypt(createPaymentDto.getCardNum())
                , aes256Util.decrypt(createPaymentDto.getExpiredDate())
                , aes256Util.decrypt(createPaymentDto.getBirthDate())
                , aes256Util.decrypt(createPaymentDto.getPwd2digit())
        );

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, price, cardInfo);
        onetimePaymentData.setPg(pgStoreId);
        onetimePaymentData.setName(paymentMsg);
        onetimePaymentData.setBuyerName(memberId);
        onetimePaymentData.setCustomer_uid(customerUid);

        return onetimePaymentData;
    }

    @Override
    public String doFirstPay(OnetimePaymentData onetimePaymentData) {
        IamportResponse<Payment> response = iamPortProvider.pay(onetimePaymentData);

        if (!iamPortProvider.checkResponse(response)) {
            log.info(response.getMessage() != null ? response.getMessage() : response.getResponse().getFailReason());
            throw new CustomException(ErrorCode.CARD_REGISTRATION_FAILED);
        }

        if (!iamPortProvider.validAmount(BigDecimal.valueOf(100), response.getResponse().getAmount())) {
            log.info("결제 금액 불일치로 인하여 결제 취소 처리 진행");
            iamPortProvider.cancelPay(
                    response.getResponse().getMerchantUid()
                    , response.getResponse().getAmount()
                    , Constants.PAYMENT_AMOUNT_MISMATCH
            );
            throw new CustomException(ErrorCode.CARD_REGISTRATION_FAILED);
        }

        return response.getResponse().getCustomerUid();
    }

    @Override
    @Transactional
    public Long registPayment(String memberId, String customerUid, String cardNum) {
        Payments payment = Payments.builder()
                .memberId(memberId)
                .customerUid(customerUid)
                .cardNum4Digit(aes256Util.encrypt(aes256Util.decrypt(cardNum).split("-")[3]))
                .build();

        return paymentRepository.save(payment).getNo();
    }

    @Override
    @Transactional(noRollbackFor = {IamportResponseException.class, IOException.class})
    public boolean doPayAgain(PaymentRequestMessage request) {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;

        Payments payment = findAndCheckMemberId(request.getPaymentNo(), request.getMemberId());
        AgainPaymentData againPaymentData = new AgainPaymentData(payment.getCustomerUid(), merchantUid, OttType.valueOfOtt(request.getOtt()).calcPrice());

        IamportResponse<Payment> response = null;

        try {
            response = iamPortProvider.payAgain(againPaymentData);
        } catch (IamportResponseException | IOException e) {
            // 통신 오류로 인한 처리
            log.error("서버 통신 오류 발생으로 인한 처리 불가능");
            int price = OttType.valueOfOtt(request.getOtt()).calcPrice().intValue();

            recordProduct(
                    payment
                    , createProduct(price, request.getPartyNo(), false)
                    , PaymentHistory.builder()
                            .status(PaymentType.PAYMENT)
                            .price(price)
                            .response("서버 통신 오류 발생으로 인한 처리 불가능")
                            .build()
            );

            return false;
        }

        // 카드에 이상이 생겨 결제를 실패했을 경우
        if (!iamPortProvider.checkResponse(response)) {
            log.info("결제 실패: " + response.getMessage() != null ? response.getMessage() : response.getResponse().getFailReason());

            PaymentHistory failPaymentHistory = createPaymentHistory(response, PaymentType.PAYMENT);
            failPaymentHistory.cancel();

            recordProduct(
                    payment
                    , createProduct(OttType.valueOfOtt(request.getOtt()).calcPrice().intValue(), request.getPartyNo(), false)
                    , failPaymentHistory
            );

            return false;
        }

        // 정상적으로 결제가 이뤄졌을 경우
        Product product = createProduct(OttType.valueOfOtt(request.getOtt()).calcPrice().intValue(), request.getPartyNo(), true);
        recordProduct(
                payment
                , product
                , createPaymentHistory(response, PaymentType.PAYMENT)
        );

        // 결제 금액이 잘못되었을 경우
        if (!iamPortProvider.validAmount(OttType.valueOfOtt(request.getOtt()).calcPrice(), response.getResponse().getAmount())) {
            log.info("결제 실패: 결제 금액 불일치로 인하여 결제 취소 처리 진행");

            IamportResponse<Payment> cancelResponse = iamPortProvider.cancelPay(
                    response.getResponse().getMerchantUid()
                    , response.getResponse().getAmount()
                    , Constants.PAYMENT_AMOUNT_MISMATCH
            );

            product.cancel();

            recordProduct(
                    null
                    , product
                    , createPaymentHistory(cancelResponse, PaymentType.ALL_CANCEL)
            );

            return false;
        }
        
        log.info(request.getMemberId() + "회원 " + request.getOtt() + "결제 성공");

        return true;
    }

    @Override
    @Transactional
    public void recordProduct(Payments payment, Product product, PaymentHistory paymentHistory) {
        if (payment != null)
            payment.addProduct(product);
        product.addPaymentHistory(paymentHistory);
    }

    private PaymentHistory createPaymentHistory(IamportResponse<Payment> response, PaymentType paymentType) {
        PaymentHistory paymentHistory = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            paymentHistory = PaymentHistory.builder()
                    .response(objectMapper.writeValueAsString(response))
                    .price(response.getResponse().getAmount().intValue())
                    .status(paymentType)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("TOPIC: " + Constants.KAFKA_TOPIC_PARTY + ", Json Parsing Fail");
        }

        return paymentHistory;
    }

    private Product createProduct(int price, Long partyNo, boolean success) {
        Product product = Product.builder()
                .partyNo(partyNo)
                .price(price)
                .build();

        if (!success)
            product.cancel();

        return product;
    }

    private Payments findAndCheckMemberId(Long paymentNo, String memberId) {
        Payments payment = paymentRepository.findById(paymentNo).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_FOUND_PAYMENT);
        });

        if (!payment.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOT_MATCH_MEMBERID);
        }

        return payment;
    }
}
