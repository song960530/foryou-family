package com.foryou.matchingservice.api;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.QueueService;
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

    @Scheduled(
            fixedRate = 500
            , initialDelay = 10000
    )
    public void FirstMatch() {
        Response test = netflix.pollQueues();
        if (test != null) {
            log.info(Thread.currentThread().getName() + ": " + test.toString());

        }
    }
}
