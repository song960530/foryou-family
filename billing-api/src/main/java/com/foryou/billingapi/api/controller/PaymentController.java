package com.foryou.billingapi.api.controller;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.Constants;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payment/{userId}")
    public void createPayment(@PathVariable String userId, @Valid @RequestBody CreatePaymentDto createPaymentDto) {
        OnetimePaymentData onetimePaymentData = paymentService.createOnetimePaymentData(userId, createPaymentDto, Constants.CHECK_CARD, BigDecimal.valueOf(100));
        IamportResponse<Payment> response = paymentService.pay(onetimePaymentData);
    }

}
