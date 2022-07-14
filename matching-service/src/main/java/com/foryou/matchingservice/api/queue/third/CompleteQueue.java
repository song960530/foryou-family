package com.foryou.matchingservice.api.queue.third;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.ThirdQueue;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Component
public class CompleteQueue implements ThirdQueue {
    private final Queue<Response> completedQueue = new LinkedList<>();

    @Override
    public void offerCompleted(Response complete) {
        completedQueue.offer(complete);
    }

    @Override
    public Optional<Response> pollQueue() {
        synchronized (this) {
            if (!completedQueue.isEmpty())
                return Optional.of(completedQueue.poll());
            return Optional.empty();
        }
    }
}
