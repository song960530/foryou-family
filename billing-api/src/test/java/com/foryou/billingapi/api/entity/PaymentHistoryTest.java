package com.foryou.billingapi.api.entity;

import com.foryou.billingapi.api.enums.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryTest {

    @Test
    @DisplayName("최초 PaymentHistory 객체 생성 성공")
    public void successCreatePaymentHistory() throws Exception {
        // given
        Product product = Product.builder().build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .product(product)
                .status(PaymentType.PAYMENT)
                .price(1000)
                .successYN(true)
                .response("response")
                .build();

        // then
        assertEquals(product, history.getProduct());
        assertEquals(PaymentType.PAYMENT, history.getStatus());
        assertEquals(1000, history.getPrice());
        assertEquals(true, history.isSuccessYN());
        assertNotNull(history.getResponse());
    }
}