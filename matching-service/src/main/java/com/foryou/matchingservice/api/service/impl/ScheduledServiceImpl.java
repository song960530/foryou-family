package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.ScheduledService;
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
            throw new CustomException(ErrorCode.NOT_EXIST_START_PEOPLE);
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
    @Transactional
    public Response secondMatchJob(Long ownerPk, Long memberPk) {
        Match owner = findStartPeople(ownerPk);
        Match member = findStartPeople(memberPk);

        owner.changeStatus(StatusType.COMPLETE);
        member.changeStatus(StatusType.COMPLETE);

        return new Response(ownerPk, memberPk);
    }

    @Override
    @Transactional
    public void thirdMatchJob(Long ownerPk, Long memberPk) {
        Match owner = findCompletePeople(ownerPk);
        Match member = findCompletePeople(memberPk);
    }
}