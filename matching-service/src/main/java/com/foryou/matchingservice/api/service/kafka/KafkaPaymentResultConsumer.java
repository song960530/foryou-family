package com.foryou.matchingservice.api.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.dto.response.PaymentResponseMessage;
import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.queue.ThirdQueue;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.MatchingService;
import com.foryou.matchingservice.global.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPaymentResultConsumer {

    private final ObjectMapper objMapper;
    private final MatchRepository repository;
    private final MatchingService matchingService;
    private final ThirdQueue thirdQueue;

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_PAYMENT_RESULT
            , groupId = Constants.KAFKA_GROPU_ID_PAYMENT
    )
    @Transactional
    public void listen(
            @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack
            , @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
            , @Header(KafkaHeaders.GROUP_ID) String groupId
            , @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
            , @Header(KafkaHeaders.OFFSET) long offset
            , @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts
            , String msg) {
        try {
            PaymentResponseMessage response = objMapper.readValue(msg, PaymentResponseMessage.class);
            log.info("message: {}, topic: {}, groupId: {}, partition: {}, offset: {}, time: {}", response, topic, groupId, partition, offset, ts);

            Match member = repository.findByMemberIdAndPartyNo(response.getMemberId(), response.getPartyNo()).get();
            Match owner = repository.findById(member.getLinkedNo()).get();

            if (response.isSuccess()) {
                member.changeStatus(StatusType.COMPLETE);
                owner.changeStatus(StatusType.COMPLETE);

                thirdQueue.offerCompleted(Response.builder()
                        .memberPk(member.getNo())
                        .ownerPk(owner.getNo())
                        .build()
                );
            } else {
                member.changeStatus(StatusType.CANCEL);
                owner.changeStatus(StatusType.CANCEL);

                List<Match> matches = matchingService.createMatch(MatchingRequestMessage.builder()
                        .partyNo(owner.getPartyNo())
                        .inwon(1)
                        .ott(owner.getOtt())
                        .role(PartyRole.OWNER)
                        .paymentNo(0L)
                        .build());

                matchingService.offerQueue(matches);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ack.acknowledge();
    }
}
