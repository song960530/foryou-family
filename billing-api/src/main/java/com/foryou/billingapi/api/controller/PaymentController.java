package com.foryou.billingapi.api.controller;

import com.foryou.billingapi.api.dto.CreatePaymentDto;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.Constants;
import com.foryou.billingapi.global.response.ApiResponse;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payments/{userId}")
    public ResponseEntity<ApiResponse> createPayment(@PathVariable String userId, @Valid @RequestBody CreatePaymentDto createPaymentDto) {
        OnetimePaymentData onetimePaymentData = paymentService.createOnetimePaymentData(userId, createPaymentDto, Constants.CHECK_CARD, BigDecimal.valueOf(100));
        String customerUid = paymentService.doFirstPay(onetimePaymentData);
        paymentService.registPayment(userId, customerUid, createPaymentDto.getCardNum());

        return ApiResponse.of(HttpStatus.CREATED);
    }
}
