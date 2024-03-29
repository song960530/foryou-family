package com.foryou.billingapi.api.controller;

import com.foryou.billingapi.api.dto.request.CreatePaymentDto;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.constants.Constants;
import com.foryou.billingapi.global.response.ApiResponse;
import com.siot.IamportRestClient.request.OnetimePaymentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }
    
    @PostMapping("/payments/{memberId}")
    public ResponseEntity<ApiResponse> createPayment(@PathVariable String memberId, @Valid @RequestBody CreatePaymentDto createPaymentDto) {
        OnetimePaymentData onetimePaymentData = paymentService.createOnetimePaymentData(memberId, createPaymentDto, Constants.CHECK_CARD, BigDecimal.valueOf(100));
        String customerUid = paymentService.doFirstPay(onetimePaymentData);
        paymentService.registPayment(memberId, customerUid, createPaymentDto.getCardNum());

        return ApiResponse.of(HttpStatus.CREATED);
    }

    @GetMapping("/payments/{memberId}")
    public ResponseEntity<ApiResponse> myPaymentCardList(@PathVariable String memberId) {

        return ApiResponse.of(HttpStatus.OK, paymentService.myPaymentCardList(memberId));
    }
}
