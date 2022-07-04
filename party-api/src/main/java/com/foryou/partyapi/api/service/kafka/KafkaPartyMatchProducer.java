package com.foryou.partyapi.api.service.kafka;

import com.foryou.partyapi.Constants;
import com.foryou.partyapi.api.dto.request.MatchingRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPartyMatchProducer {

    @Qualifier("KafkaTemplatePartyRequest")
    private final KafkaTemplate<String, MatchingRequestMessage> kafkaTemplate;

    public void sendMessage(MatchingRequestMessage message) {
        kafkaTemplate.send(Constants.KAFKA_TOPIC_PARTY, message);
    }
}
