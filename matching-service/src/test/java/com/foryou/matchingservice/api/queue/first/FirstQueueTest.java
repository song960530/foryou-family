package com.foryou.matchingservice.api.queue.first;

import com.foryou.matchingservice.api.queue.FirstQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FirstQueueTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstQueueTest.class);
    private List<FirstQueue> firstQueues;

    @BeforeEach
    void setUp() {
        firstQueues = new ArrayList<>();
        firstQueues.add(new Netflix());
        firstQueues.add(new Tving());
        firstQueues.add(new Disney());
        firstQueues.add(new Watcha());
        firstQueues.add(new Wavve());
    }

    @Test
    @DisplayName("Queue에 offer는 여러 쓰레드가 동시에 가능하다")
    public void canOfferAsTheSameTime() throws Exception {
        firstQueues.forEach(queue -> {
            // given
            Thread before = new Thread(() -> {
                LOGGER.debug(Thread.currentThread().getName() + Thread.currentThread().getId() + ": 1L IN");
                queue.offerMember(1L);
                queue.offerOwner(1L);
                LOGGER.debug(Thread.currentThread().getName() + Thread.currentThread().getId() + ": 1L FINISH");
            });

            Thread after = new Thread(() -> {
                LOGGER.debug(Thread.currentThread().getName() + Thread.currentThread().getId() + ": 2L IN");
                queue.offerMember(2L);
                queue.offerOwner(2L);
                LOGGER.debug(Thread.currentThread().getName() + Thread.currentThread().getId() + ": 2L FINISH");
            });

            // when
            before.start();
            after.start();
            try {
                before.join();
                after.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // then
            assertEquals(2, queue.memberQueueSize());
            assertEquals(2, queue.ownerQueueSize());
        });
    }

    @Test
    @DisplayName("Queue에서 poll은 동시에 불가능 하다")
    public void canNotPollAsTheSameTime() throws Exception {
        firstQueues.forEach(queue -> {
            // given
            Thread init = new Thread(() -> {
                queue.offerMember(1L);
                queue.offerOwner(1L);
                queue.offerMember(2L);
                queue.offerOwner(2L);
            });
            Thread threadA = new Thread(() -> queue.pollQueues());
            Thread threadB = new Thread(() -> queue.pollQueues());

            // when
            try {
                init.start();
                init.join();
                threadA.start();
                threadB.start();
                threadA.join();
                threadB.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // then
            assertEquals(0, queue.memberQueueSize());
            assertEquals(0, queue.ownerQueueSize());
            assertEquals(Optional.empty(), queue.pollQueues());
        });
    }
}