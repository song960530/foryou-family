package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;

public interface FirstQueue {
    public void offerMember(Long no);

    public void offerOwner(Long no);

    public Response pollQueues();
}
