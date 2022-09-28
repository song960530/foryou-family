package com.foryou.partyapi.api.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.partyapi.api.dto.response.MatchingResponseMessage;
import com.foryou.partyapi.api.service.PartyService;
import com.foryou.partyapi.global.constants.Constants;
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
    private final PartyService partyService;

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_MATCH_RESULT
            , groupId = Constants.KAFKA_GROPU_ID_MATCH_RESULT
    )
    public void listen(
            @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack
            , @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
            , @Header(KafkaHeaders.GROUP_ID) String groupId
            , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
            , @Header(KafkaHeaders.OFFSET) long offset
            , @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts
            , String msg) {

        MatchingResponseMessage request = null;
        try {
            request = objMapper.readValue(msg, MatchingResponseMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("message: {}, topic: {}, groupId: {}, partition: {}, offset: {}, time: {}", request, topic, groupId, partition, offset, ts);
        partyService.finishMatch(request);

        ack.acknowledge();
    }
}
