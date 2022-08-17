package com.foryou.matchingservice.api;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.FirstQueue;
import com.foryou.matchingservice.api.queue.SecondQueue;
import com.foryou.matchingservice.api.queue.ThirdQueue;
import com.foryou.matchingservice.api.service.ScheduledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetfilxFirstScheduled {

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
    private final ScheduledService service;

    @Scheduled(
            fixedRate = 500
            , initialDelay = 10000
    )
    public void FirstMatch() {
        netflix.pollQueues()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Netflix First Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    secondQueue.offerMatched(matched);
                });

        tving.pollQueues()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Tving First Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    secondQueue.offerMatched(matched);
                });

        disney.pollQueues()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Disney First Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    secondQueue.offerMatched(matched);
                });

        watcha.pollQueues()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Watcha First Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    secondQueue.offerMatched(matched);
                });

        wavve.pollQueues()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Wavve First Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    secondQueue.offerMatched(matched);
                });
    }

    @Scheduled(
            fixedRate = 500
            , initialDelay = 5000
    )
    public void SecondMatch() {
        secondQueue.pollQueue()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Second Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    Response completed = service.secondMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                    thirdQueue.offerCompleted(completed);
                });
    }

    @Scheduled(
            fixedRate = 500
            , initialDelay = 2000
    )
    public void ThirdMatch() {
        thirdQueue.pollQueue()
                .ifPresent(pollQueue -> {
                    log.info("{}: [Third Match] OwnerPk: {}, MemberPk: {}", Thread.currentThread().getName(), pollQueue.getOwnerPk(), pollQueue.getMemberPk());

                    service.thirdMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
                });
    }
}
