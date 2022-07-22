package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.queue.first.Netflix;
import com.foryou.matchingservice.api.repository.InitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class InitServiceImplTest {

    @InjectMocks
    private InitServiceImpl service;
    @Mock
    private InitRepository initRepository;
    @Spy
    private Netflix netflix;


    @Test
    @DisplayName("미처리된 데이터가 있으면 재기동시 Queue에 저장한다")
    public void uploadUnprocessDataInQueue() throws Exception {
        // given
        List<Long> noList = List.of(1L, 2L, 3L);

        doReturn(noList).when(initRepository).selectUnprocessedWait(any(OttType.class), any(PartyRole.class));

        // when
        service.init();

        // then
        assertEquals(3, netflix.memberQueueSize());
        assertEquals(3, netflix.ownerQueueSize());
    }

    @Test
    @DisplayName("미처리된 데이터가 없으면 재기동시 Queue는 비어있다")
    public void emptyNoExistUnprocessData() throws Exception {
        // given
        List<Long> noList = List.of();

        doReturn(noList).when(initRepository).selectUnprocessedWait(any(OttType.class), any(PartyRole.class));

        // when
        service.init();

        // then
        assertEquals(0, netflix.memberQueueSize());
        assertEquals(0, netflix.ownerQueueSize());
    }

}