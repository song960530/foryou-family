package com.foryou.matchingservice.api.queue;

import com.foryou.matchingservice.api.dto.response.Response;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

public class WaitQueue {
    @Component
    public static class Netflix {
        private final Queue<Long> memberQueue = new LinkedList<>();
        private final Queue<Long> ownerQueue = new LinkedList<>();

        public void offerMember(Long no) {
            memberQueue.offer(no);
        }

        public void offerOwner(Long no) {
            ownerQueue.offer(no);
        }

        public synchronized Response pollQueues() {
            if (!ownerQueue.isEmpty() && !memberQueue.isEmpty())
                return new Response(memberQueue.poll(), ownerQueue.poll());
            return null;
        }
    }
}
