package com.foryou.matchingservice.api.service.kafka;

import com.foryou.matchingservice.api.dto.response.MatchingResultMessage;
import com.foryou.matchingservice.global.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMatchResultProducer {

    @Qualifier("KafkaTemplateMatchingResult")
    private final KafkaTemplate<String, MatchingResultMessage> kafkaTemplate;

    public void sendMessage(MatchingResultMessage message) {
        kafkaTemplate.send(Constants.KAFKA_TOPIC_MATCH_RESULT, message);
    }
}
