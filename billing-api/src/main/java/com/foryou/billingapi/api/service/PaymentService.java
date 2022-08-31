package com.foryou.billingapi.api.service;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.siot.IamportRestClient.request.OnetimePaymentData;

import java.math.BigDecimal;

public interface PaymentService {
    OnetimePaymentData createOnetimePaymentData(String userId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price);

    String doFirstPay(OnetimePaymentData onetimePaymentData);

    Long registPayment(String userId, String customerUid, String cardNum);
}
