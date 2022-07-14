package com.foryou.matchingservice.api;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.QueueService;
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
    private final QueueService netflix;
    private final ScheduledService service;

    @Scheduled(
            fixedRate = 500
            , initialDelay = 10000
    )
    public void FirstMatch() {
        Response pollQueue = netflix.pollQueues();
        if (pollQueue != null) {
            log.info(Thread.currentThread().getName() + ": " + pollQueue.toString());

            Response matchedPk = service.firstMatchJob(pollQueue.getOwnerPk(), pollQueue.getMemberPk());
        }
    }
}
