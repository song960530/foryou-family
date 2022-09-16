package com.foryou.billingapi.api.service.kafka;

import com.foryou.billingapi.api.dto.response.PaymentResponseMessage;
import com.foryou.billingapi.global.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPaymentResultProducer {

    @Qualifier("KafkaTemplatePaymentResult")
    private final KafkaTemplate<String, PaymentResponseMessage> kafkaTemplate;

    public void sendMessage(PaymentResponseMessage message) {
        kafkaTemplate.send(Constants.KAFKA_TOPIC_PAYMENT_RESULT, message);
    }
}
