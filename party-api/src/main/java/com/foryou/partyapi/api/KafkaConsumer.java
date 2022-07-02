package com.foryou.partyapi.api;

import com.foryou.partyapi.Constants;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(
            topics = Constants.TOPIC_PARTY
            , groupId = "party_group"
    )
    public void listen(String msg) {
        System.out.println("1: " + msg);
    }

    @KafkaListener(
            topics = Constants.TOPIC_PARTY
            , groupId = "party_group"
    )
    public void listen2(String msg) {
        System.out.println("2: " + msg);
    }
}
