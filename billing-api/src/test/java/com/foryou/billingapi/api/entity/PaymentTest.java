package com.foryou.billingapi.api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PaymentTest {

    private Payments payment;

    @BeforeEach
    void setUp() {
        String userId = "test123";
        String cardNum4Digit = "1234";
        String customerUid = "test-test-test-test-test-test-test";
        this.payment = Payments.builder()
                .userId(userId)
                .cardNum4Digit(cardNum4Digit)
                .customerUid(customerUid)
                .build();
    }

    @Test
    @DisplayName("최초 Payment 객체 정상 생성")
    public void successCreatePayment() throws Exception {
        // given
        String userId = "test123";
        String cardNum4Digit = "1234";
        String customerUid = "test-test-test-test-test-test-test";

        // when

        // then
        assertEquals(userId, payment.getUserId());
        assertEquals(cardNum4Digit, payment.getCardNum4Digit());
        assertEquals(customerUid, payment.getCustomerUid());
        assertEquals(false, payment.isDelYN());
        assertEquals(0, payment.getProducts().size());
    }

    @Test
    @DisplayName("Payment delete 변경 확인")
    public void paymentDelete() throws Exception {
        // given

        // when
        payment.delete();

        // then
        assertEquals(true, payment.isDelYN());
    }

    @Test
    @DisplayName("products 연관관계 메서드 확인")
    public void associationProducts() throws Exception {
        // given
        Product product = Product.builder()
                .price(1000)
                .partyNo(1L)
                .build();

        // when
        payment.addProduct(product);

        // then
        assertNotNull(product.getPayment());
        assertEquals(payment, product.getPayment());
        assertEquals(product, payment.getProducts().get(0));
    }
}