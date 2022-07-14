package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

public interface SecondQueue {

    void offerMatched(Response matched);

    Response pollQueue();
}
