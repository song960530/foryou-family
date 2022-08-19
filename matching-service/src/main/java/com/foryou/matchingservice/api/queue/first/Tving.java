package com.foryou.matchingservice.api.queue.first;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.queue.FirstQueue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@Qualifier("Tving")
@Component
public class Tving implements FirstQueue {
    private final Queue<Long> memberQueue = new LinkedList<>();
    private final Queue<Long> ownerQueue = new LinkedList<>();

    @Override
    public void offerMember(Long no) {
        memberQueue.offer(no);
    }

    @Override
    public void offerOwner(Long no) {
        ownerQueue.offer(no);
    }

    @Override
    public int memberQueueSize() {
        return memberQueue.size();
    }

    @Override
    public int ownerQueueSize() {
        return ownerQueue.size();
    }

    @Override
    public Optional<Response> pollQueues() {
        synchronized (this) {
            if (!ownerQueue.isEmpty() && !memberQueue.isEmpty())
                return Optional.of(Response.builder()
                        .ownerPk(ownerQueue.poll())
                        .memberPk(memberQueue.poll())
                        .build()
                );
            return Optional.empty();
        }
    }
}
