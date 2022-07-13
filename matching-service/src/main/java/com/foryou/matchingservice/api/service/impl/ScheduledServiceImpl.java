package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.ScheduledService;
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


    private Match findWaitPeople(Long no) {
        return repository.findByNoAndStatus(no, StatusType.WAIT).orElseThrow(() -> {
            // TODO: CustomException 처리
        });
    }

    @Override
    @Transactional
    public void changeStatus(Long ownerNo, Long memberNo) {
        Match owner = findWaitPeople(ownerNo);
        Match member = findWaitPeople(memberNo);

        owner.changeStatus(StatusType.START);
        member.changeStatus(StatusType.START);
    }
}
