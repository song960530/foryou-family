package com.foryou.matchingservice.api;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.FirstQueue;
import com.foryou.matchingservice.api.queue.SecondQueue;
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
    private final SecondQueue secondQueue;
    private final ScheduledService service;

    @Scheduled(
            fixedRate = 500
            , initialDelay = 10000
    )
    public void FirstMatch() {
        Response pollQueue = netflix.pollQueues();
        if (pollQueue != null) {
            log.info(Thread.currentThread().getName() + ": " + pollQueue.toString());

            Response matched = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
            secondQueue.offerMatched(matched);
        }
    }
}
