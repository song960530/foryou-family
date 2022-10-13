package com.foryou.billingapi.api.service;

import com.foryou.billingapi.api.dto.request.CreatePaymentDto;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.dto.response.CardListResDto;
import com.foryou.billingapi.api.entity.PaymentHistory;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.entity.Product;
import com.siot.IamportRestClient.request.OnetimePaymentData;

import java.math.BigDecimal;

public interface PaymentService {
    OnetimePaymentData createOnetimePaymentData(String memberId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price);

    String doFirstPay(OnetimePaymentData onetimePaymentData);

    Long registPayment(String memberId, String customerUid, String cardNum);

    boolean doPayAgain(PaymentRequestMessage request);

    void recordProduct(Payments payment, Product product, PaymentHistory paymentHistory);

    CardListResDto myPaymentCardList(String memberId);
}
