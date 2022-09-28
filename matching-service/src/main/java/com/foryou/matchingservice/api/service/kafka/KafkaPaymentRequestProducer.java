package com.foryou.matchingservice.api.service.kafka;

import com.foryou.matchingservice.api.dto.request.PaymentRequestMessage;
import com.foryou.matchingservice.global.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPaymentRequestProducer {

    @Qualifier("KafkaPaymentRequestTemplate")
    private final KafkaTemplate<String, PaymentRequestMessage> kafkaTemplate;

    public void sendMessage(PaymentRequestMessage message) {
        kafkaTemplate.send(Constants.KAFKA_TOPIC_PAYMENT, message);
    }
}
