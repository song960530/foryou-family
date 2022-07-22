package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.queue.FirstQueue;
import com.foryou.matchingservice.api.repository.InitRepository;
import com.foryou.matchingservice.api.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InitServiceImpl implements InitService {

    private final InitRepository initRepository;
    @Qualifier("Netflix")
    private final FirstQueue netflix;

    /**
     * 서비스 재기동 시 미처리건 Queue에 저장
     */
    @PostConstruct
    public void init() {
        uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.OWNER);
        uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.MEMBER);
        uploadStartUnprocessData();
    }

    @Override
    public void uploadStartUnprocessData() {
        log.info("START Status {} Unprocessed Data Upload", StatusType.START);

        List<Response> responses = initRepository.selectUnprocessedStart();
        // TODO: 이거 init함수 따로 파일 뺴야할까...?

        log.info("END Status {} Unprocessed Data Upload", StatusType.START);

    }

    @Override
    public void uploadWaitUnprocessData(OttType ott, PartyRole role) {
        log.info("START Status {} Unprocessed Data Upload ({}, {})", StatusType.WAIT, ott, role);

        List<Long> noList = initRepository
                .selectUnprocessedWait(ott, role);

        if (PartyRole.MEMBER.equals(role)) {
            noList.forEach(no -> netflix.offerMember(no));
        } else {
            noList.forEach(no -> netflix.offerOwner(no));
        }

        log.info("END Status {} Unprocessed Data Upload ({}, {}): {}", StatusType.WAIT, ott, role, noList.size());
    }
}
