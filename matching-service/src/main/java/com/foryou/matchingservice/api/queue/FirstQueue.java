package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

public interface FirstQueue {
    void offerMember(Long no);

    void offerOwner(Long no);

    Response pollQueues();
}
