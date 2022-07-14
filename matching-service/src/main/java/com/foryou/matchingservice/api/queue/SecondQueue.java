package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

import java.util.Optional;

public interface SecondQueue {

    void offerMatched(Response matched);

    Optional<Response> pollQueue();
}
