package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

import java.util.Optional;

public interface FirstQueue {
    void offerMember(Long no);

    void offerOwner(Long no);

    Optional<Response> pollQueues();

    int memberQueueSize();

    int ownerQueueSize();
}
