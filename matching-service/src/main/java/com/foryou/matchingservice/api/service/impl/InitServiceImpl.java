package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.queue.FirstQueue;
import com.foryou.matchingservice.api.queue.SecondQueue;
import com.foryou.matchingservice.api.queue.ThirdQueue;
import com.foryou.matchingservice.api.repository.InitRepository;
import com.foryou.matchingservice.api.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InitServiceImpl implements InitService {

    private final InitRepository initRepository;
    @Qualifier("Netflix")
    private final FirstQueue netflix;
    @Qualifier("Tving")
    private final FirstQueue tving;
    @Qualifier("Disney")
    private final FirstQueue disney;
    @Qualifier("Watcha")
    private final FirstQueue watcha;
    @Qualifier("Wavve")
    private final FirstQueue wavve;
    private final SecondQueue secondQueue;
    private final ThirdQueue thirdQueue;

    /**
     * 서비스 재기동 시 미처리건 Queue에 저 장
     */
    @PostConstruct
    private void init() {
        Arrays.stream(OttType.values()).forEach(ott -> {
            uploadWaitUnprocessData(ott, PartyRole.OWNER);
            uploadWaitUnprocessData(ott, PartyRole.MEMBER);
        });
        uploadStartUnprocessData();
        uploadCompleteUnprocessData();
    }

    @Override
    public void uploadCompleteUnprocessData() {
        log.info("START Status {} Unprocessed Data Upload", StatusType.COMPLETE);

        List<Response> responses = initRepository.selectUnprocessedAfterWait(StatusType.COMPLETE);

        responses.forEach(match -> thirdQueue.offerCompleted(match));

        log.info("END Status {} Unprocessed Data Upload: {}", StatusType.COMPLETE, responses.size());
    }

    @Override
    public void uploadStartUnprocessData() {
        log.info("START Status {} Unprocessed Data Upload", StatusType.START);

        List<Response> responses = initRepository.selectUnprocessedAfterWait(StatusType.START);

        responses.forEach(match -> secondQueue.offerMatched(match));

        log.info("END Status {} Unprocessed Data Upload: {}", StatusType.START, responses.size());

    }

    @Override
    public void uploadWaitUnprocessData(OttType ott, PartyRole role) {
        log.info("START Status {} Unprocessed Data Upload ({}, {})", StatusType.WAIT, ott, role);

        List<Long> noList = initRepository.selectUnprocessedWait(ott, role);

        if (PartyRole.MEMBER.equals(role)) {
            switch (ott) {
                case NETFLIX:
                    noList.forEach(no -> netflix.offerMember(no));
                    break;
                case TVING:
                    noList.forEach(no -> tving.offerMember(no));
                    break;
                case WAVVE:
                    noList.forEach(no -> wavve.offerMember(no));
                    break;
                case WATCHA:
                    noList.forEach(no -> watcha.offerMember(no));
                    break;
                case DISNEY_PLUS:
                    noList.forEach(no -> disney.offerMember(no));
                    break;
            }
        } else {
            switch (ott) {
                case NETFLIX:
                    noList.forEach(no -> netflix.offerOwner(no));
                    break;
                case TVING:
                    noList.forEach(no -> tving.offerOwner(no));
                    break;
                case WAVVE:
                    noList.forEach(no -> wavve.offerOwner(no));
                    break;
                case WATCHA:
                    noList.forEach(no -> watcha.offerOwner(no));
                    break;
                case DISNEY_PLUS:
                    noList.forEach(no -> disney.offerOwner(no));
                    break;
            }
        }

        log.info("END Status {} Unprocessed Data Upload ({}, {}): {}", StatusType.WAIT, ott, role, noList.size());
    }
}
