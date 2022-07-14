package com.foryou.matchingservice.api.queue.second;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.SecondQueue;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Component
public class MatchQueue implements SecondQueue {
    private final Queue<Response> matchedQueue = new LinkedList<>();

    @Override
    public void offerMatched(Response matched) {
        matchedQueue.offer(matched);
    }

    @Override
    public Optional<Response> pollQueue() {
        synchronized (this) {
            if (!matchedQueue.isEmpty())
                return Optional.of(matchedQueue.poll());
            return Optional.empty();
        }
    }
}
