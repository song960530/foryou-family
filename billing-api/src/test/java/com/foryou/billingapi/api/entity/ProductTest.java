package com.foryou.billingapi.api.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductTest {

    @Test
    @DisplayName("최초 Product 객체 생성")
    public void createProductSuccess() throws Exception {
        // given
        Product product = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        // when

        // then
        assertEquals(1, product.getPartyNo());
        assertEquals(1000, product.getPrice());
        assertEquals(false, product.isCancelYN());
        assertNotNull(product.getDueDate());
        assertNull(product.getPayment());
    }

    @Test
    @DisplayName("28일 이전 결제시 동일한 날짜에 정기 결제")
    public void checkDueDateBefore28th() throws Exception {
        // given
        Product product = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        ReflectionTestUtils.setField(product, "joinDate", LocalDate.of(2022, 2, 27));

        // when
        product.calcNextDueDate();

        // then
        assertEquals(LocalDate.of(2022, 3, 27).toString(), product.getDueDate().toString());
    }

    @Test
    @DisplayName("1월29일 결제시 평년이면 28, 윤년이면 29에 결제")
    public void checkDueDate29th() throws Exception {
        // given
        Product product1 = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        ReflectionTestUtils.setField(product1, "joinDate", LocalDate.of(2022, 1, 29));

        Product product2 = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        ReflectionTestUtils.setField(product2, "joinDate", LocalDate.of(2020, 1, 29));

        // when
        product1.calcNextDueDate();
        product2.calcNextDueDate();

        // then
        assertEquals(LocalDate.of(2022, 2, 28).toString(), product1.getDueDate().toString());
        assertEquals(LocalDate.of(2020, 2, 29).toString(), product2.getDueDate().toString());
    }

    @Test
    @DisplayName("31일 결제시 매월 마지막날 결제")
    public void checkDueDate31th() throws Exception {
        // given
        Product product1 = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        ReflectionTestUtils.setField(product1, "joinDate", LocalDate.of(2022, 7, 31));

        // when
        product1.calcNextDueDate();
        // then
        assertEquals(LocalDate.of(2022, 8, 31).toString(), product1.getDueDate().toString());

        // when
        product1.calcNextDueDate();
        // then
        assertEquals(LocalDate.of(2022, 9, 30).toString(), product1.getDueDate().toString());
    }

    @Test
    @DisplayName("PaymentHistory add 테스트")
    public void successPaymentHistoryAdd() throws Exception {
        // given
        Product product1 = Product.builder()
                .partyNo(1L)
                .price(1000)
                .build();

        PaymentHistory paymentHistory = PaymentHistory.builder().build();

        // when
        product1.addPaymentHistory(paymentHistory);

        // then
        assertNotNull(product1.getPaymentHistories());
        assertEquals(1, product1.getPaymentHistories().size());
        assertEquals(paymentHistory, product1.getPaymentHistories().get(0));
    }
}