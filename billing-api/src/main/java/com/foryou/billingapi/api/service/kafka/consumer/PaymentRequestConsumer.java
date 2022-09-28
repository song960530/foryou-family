package com.foryou.billingapi.api.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.dto.response.PaymentResponseMessage;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.api.service.kafka.producer.KafkaProducer;
import com.foryou.billingapi.global.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestConsumer {

    private final ObjectMapper objMapper;
    private final PaymentService paymentService;
    private final KafkaProducer producer;

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_PARTY
            , groupId = Constants.KAFKA_GROPU_ID_PAYMENT
    )
    public void listen(
            @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack
            , @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
            , @Header(KafkaHeaders.GROUP_ID) String groupId
            , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
            , @Header(KafkaHeaders.OFFSET) long offset
            , @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts
            , String msg) {
        try {
            PaymentRequestMessage request = objMapper.readValue(msg, PaymentRequestMessage.class);
            log.info("message: {}, topic: {}, groupId: {}, partition: {}, offset: {}, time: {}", request, topic, groupId, partition, offset, ts);

            PaymentResponseMessage resultMessage = createResultMessage(request, paymentService.doPayAgain(request));
            producer.sendMessage(Constants.KAFKA_TOPIC_PAYMENT_RESULT, resultMessage);
        } catch (JsonProcessingException e) {
            log.error("파싱 오류 발생");
        } finally {
            ack.acknowledge();
        }
    }

    private PaymentResponseMessage createResultMessage(PaymentRequestMessage request, boolean isSuccess) {
        return PaymentResponseMessage.builder()
                .memberId(request.getMemberId())
                .partyNo(request.getPartyNo())
                .paymentNo(request.getPaymentNo())
                .success(isSuccess)
                .build();
    }
}
