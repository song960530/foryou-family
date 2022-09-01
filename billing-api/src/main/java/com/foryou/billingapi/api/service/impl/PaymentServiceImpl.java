package com.foryou.billingapi.api.service.impl;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.repository.PaymentRepository;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.Constants;
import com.foryou.billingapi.global.crypto.AES256Util;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
import com.foryou.billingapi.global.iamport.IamPortProvider;
import com.siot.IamportRestClient.request.CardInfo;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
