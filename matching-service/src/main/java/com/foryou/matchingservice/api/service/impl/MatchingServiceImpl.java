package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.queue.WaitQueue.Netflix;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final MatchRepository matchRepository;
    private final Netflix netflix;

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
