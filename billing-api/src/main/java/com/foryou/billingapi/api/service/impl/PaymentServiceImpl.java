package com.foryou.billingapi.api.service.impl;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.Constants;
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

    @Override
    public OnetimePaymentData createOnetimePaymentData(String userId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price) {
        long milliSeconds = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String merchantUid = Constants.MERCHANT_UID_PREFIX + Constants.UNDER_BAR + milliSeconds;
        String customerUid = userId + Constants.UNDER_BAR + milliSeconds;
        String pgStoreId = Constants.PG_TYPE_KCP + Constants.COMMA + Constants.KCP_STORE_ID;

        CardInfo cardInfo = new CardInfo(
                createPaymentDto.getCardNum()
                , createPaymentDto.getExpiredDate()
                , createPaymentDto.getBirthDate()
                , createPaymentDto.getPwd2digit()
        );

        OnetimePaymentData onetimePaymentData = new OnetimePaymentData(merchantUid, price, cardInfo);
        onetimePaymentData.setPg(pgStoreId);
        onetimePaymentData.setName(paymentMsg);
        onetimePaymentData.setBuyerName(userId);
        onetimePaymentData.setCustomer_uid(customerUid);

        return onetimePaymentData;
    }

    @Override
    @Transactional
    public IamportResponse<Payment> pay(OnetimePaymentData onetimePaymentData) {
        IamportResponse<Payment> response = iamPortProvider.pay(onetimePaymentData);

        if (!iamPortProvider.checkResponse(response)) {
            log.info(response.getMessage() != null ? response.getMessage() : response.getResponse().getFailReason());
            throw new CustomException(ErrorCode.CARD_REGISTRATION_FAILED);
        }

        return null;
    }
}
