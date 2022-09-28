package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.PaymentRequestMessage;
import com.foryou.matchingservice.api.dto.response.MatchingResultMessage;
import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.ScheduledService;
import com.foryou.matchingservice.api.service.kafka.KafkaProducer;
import com.foryou.matchingservice.global.constants.Constants;
import com.foryou.matchingservice.global.error.CustomException;
import com.foryou.matchingservice.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduledServiceImpl implements ScheduledService {

    private final MatchRepository repository;
    private final KafkaProducer testProducer;


    private Match findWaitPeople(Long no) {
        return repository.findByNoAndStatus(no, StatusType.WAIT).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_EXIST_WAIT_PEOPLE);
        });
    }

    private Match findStartPeople(Long no) {
        return repository.findByNoAndStatus(no, StatusType.START).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_EXIST_START_PEOPLE);
        });
    }

    private Match findCompletePeople(Long no) {
        return repository.findByNoAndStatus(no, StatusType.COMPLETE).orElseThrow(() -> {
            throw new CustomException(ErrorCode.NOT_EXIST_COMPLETE_PEOPLE);
        });
    }

    @Override
    @Transactional
    public Response firstMatchJob(Long ownerPk, Long memberPk) {
        Match owner = findWaitPeople(ownerPk);
        Match member = findWaitPeople(memberPk);

        owner.changeStatus(StatusType.START);
        member.changeStatus(StatusType.START);

        owner.link(memberPk);
        member.link(ownerPk);

        return new Response(ownerPk, memberPk);
    }

    @Override
    public void secondMatchJob(Long ownerPk, Long memberPk) {
        Match member = findStartPeople(memberPk);
        testProducer.sendMessage(Constants.KAFKA_TOPIC_PAYMENT, createPaymentRequestMessage(member));
    }

    @Override
    @Transactional
    public void thirdMatchJob(Long ownerPk, Long memberPk) {
        Match owner = findCompletePeople(ownerPk);
        Match member = findCompletePeople(memberPk);

        testProducer.sendMessage(Constants.KAFKA_TOPIC_MATCH_RESULT, createResultMessage(owner, member));

        owner.changeStatus(StatusType.ALL_COMPLETE);
        member.changeStatus(StatusType.ALL_COMPLETE);
    }

    private PaymentRequestMessage createPaymentRequestMessage(Match member) {
        return PaymentRequestMessage.builder()
                .memberId(member.getMemberId())
                .partyNo(member.getPartyNo())
                .paymentNo(member.getPaymentNo())
                .ott(member.getOtt())
                .build();
    }

    private MatchingResultMessage createResultMessage(Match owner, Match member) {
        return MatchingResultMessage.builder()
                .ownerNo(owner.getPartyNo())
                .memberNo(member.getPartyNo())
                .ott(owner.getOtt())
                .build();
    }
}