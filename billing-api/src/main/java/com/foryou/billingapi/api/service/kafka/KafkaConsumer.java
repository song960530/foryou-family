package com.foryou.billingapi.api.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.billingapi.api.dto.request.PaymentRequestMessage;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.Constants;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
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
public class KafkaConsumer {

    private final ObjectMapper objMapper;
    private final PaymentService paymentService;

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
        PaymentRequestMessage request = null;
        try {
            request = objMapper.readValue(msg, PaymentRequestMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("message: {}, topic: {}, groupId: {}, partition: {}, offset: {}, time: {}", request, topic, groupId, partition, offset, ts);

        try {
            IamportResponse<Payment> response = paymentService.doPayAgain(request);
        } catch (IamportResponseException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        ack.acknowledge();
    }
}
