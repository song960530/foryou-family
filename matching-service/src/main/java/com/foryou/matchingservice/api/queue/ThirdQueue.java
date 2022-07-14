package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

import java.util.Optional;

public interface ThirdQueue {
    void offerCompleted(Response complete);

    Optional<Response> pollQueue();
}
