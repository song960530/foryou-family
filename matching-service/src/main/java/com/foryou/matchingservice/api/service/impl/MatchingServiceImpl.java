package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.queue.FirstQueue;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchRepository matchRepository;
    @Qualifier("Netflix")
    private final FirstQueue netflix;

    /**
     * 서비스 재기동 시 미처리건 Queue에 저장
     */
    @PostConstruct
    public void init() {
        uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.OWNER);
        uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.MEMBER);
    }

    private void uploadWaitUnprocessData(OttType ott, PartyRole role) {
        log.info("START Unprocessed Data Upload ({}, {})", ott, role);

        List<Long> noList = matchRepository
                .selectUnprocessedWait(ott, role);

        if (PartyRole.MEMBER.equals(role)) {
            noList.forEach(no -> netflix.offerMember(no));
        } else {
            noList.forEach(no -> netflix.offerOwner(no));
        }

        log.info("END Unprocessed Data Upload ({}, {}): {}", ott, role, noList.size());
    }

    /**
     * 요청 인원수 개수만큼 생성
     */
    @Override
    @Transactional
    public List<Match> createMatch(MatchingRequestMessage request) {
        return Stream.iterate(0, i -> i + 1)
                .map(i -> request.toEntity())
                .map(entity -> matchRepository.save(entity))
                .limit(request.getInwon())
                .collect(Collectors.toList());
    }

    @Override
    public void offerQueue(List<Match> matches) {
        matches.stream().forEach(match -> {
            if (PartyRole.MEMBER == match.getRole()) {
                offerMemberQueue(match.getNo(), match.getOtt());
            } else {
                offerOwnerQueue(match.getNo(), match.getOtt());
            }
        });
    }

    private void offerMemberQueue(Long no, OttType ott) {
        switch (ott) {
            case NETFLIX:
                netflix.offerMember(no);
                break;
            case TVING:
                break;
            case DISNEY_PLUS:
                break;
            case WATCHA:
                break;
            case WAVVE:
                break;
        }
    }

    private void offerOwnerQueue(Long no, OttType ott) {
        switch (ott) {
            case NETFLIX:
                netflix.offerOwner(no);
                break;
            case TVING:
                break;
            case DISNEY_PLUS:
                break;
            case WATCHA:
                break;
            case WAVVE:
                break;
        }
    }
}
