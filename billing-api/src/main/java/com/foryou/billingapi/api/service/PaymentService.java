package com.foryou.billingapi.api.service;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.entity.PaymentHistory;
import com.foryou.billingapi.api.entity.Payments;
import com.foryou.billingapi.api.entity.Product;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

public interface PaymentService {
    OnetimePaymentData createOnetimePaymentData(String memberId, CreatePaymentDto createPaymentDto, String paymentMsg, BigDecimal price);

    String doFirstPay(OnetimePaymentData onetimePaymentData);

    Long registPayment(String memberId, String customerUid, String cardNum);

    @Transactional
    boolean doPayAgain(PaymentRequestMessage request) throws IamportResponseException, IOException;

    @Transactional
    void recordProduct(Payments payment, Product product, PaymentHistory paymentHistory);
}
