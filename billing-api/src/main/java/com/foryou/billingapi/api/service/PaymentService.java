package com.foryou.billingapi.api.service;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import java.math.BigDecimal;

public interface PaymentService {
    OnetimePaymentData createOnetimePaymentData(String userId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price);

    IamportResponse<Payment> pay(OnetimePaymentData onetimePaymentData);
}
