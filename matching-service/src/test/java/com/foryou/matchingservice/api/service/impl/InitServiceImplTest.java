package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.queue.first.Netflix;
import com.foryou.matchingservice.api.queue.second.MatchQueue;
import com.foryou.matchingservice.api.queue.third.CompleteQueue;
import com.foryou.matchingservice.api.repository.InitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    @Spy
    private MatchQueue secondQueue;
    @Spy
    private CompleteQueue thirdQueue;


    @Test
    @DisplayName("미처리된 Member 데이터가 있으면 재기동시 Queue에 저장한다")
    public void uploadUnprocessDataInMemberQueue() throws Exception {
        // given
        List<Long> noList = List.of(1L, 2L, 3L);

        doReturn(noList).when(initRepository).selectUnprocessedWait(any(OttType.class), any(PartyRole.class));

        // when
        service.uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.MEMBER);

        // then
        assertEquals(3, netflix.memberQueueSize());
    }

    @Test
    @DisplayName("미처리된 Owner 데이터가 있으면 재기동시 Queue에 저장한다")
    public void uploadUnprocessDataInOwnerQueue() throws Exception {
        // given
        List<Long> noList = List.of(1L, 2L, 3L);

        doReturn(noList).when(initRepository).selectUnprocessedWait(any(OttType.class), any(PartyRole.class));

        // when
        service.uploadWaitUnprocessData(OttType.NETFLIX, PartyRole.OWNER);

        // then
        assertEquals(3, netflix.ownerQueueSize());
    }

    @Test
    @DisplayName("미처리된 데이터가 없으면 재기동시 Queue는 비어있다")
    public void emptyNoExistUnprocessData() throws Exception {
        // given
        doReturn(new ArrayList<Response>()).when(initRepository).selectUnprocessedAfterWait(StatusType.START);

        // when
        service.uploadStartUnprocessData();

        // then
        assertEquals(0, netflix.memberQueueSize());
        assertEquals(0, netflix.ownerQueueSize());
    }

    @Test
    @DisplayName("미처리된 START상태의 데이터가 있으면 재기동시 Queue에 저장된다")
    public void uploadUnprocessSTARTdataInQueue() throws Exception {
        // given
        Response response = Response.builder()
                .ownerPk(1L)
                .memberPk(2L)
                .build();

        doReturn(List.of(response)).when(initRepository).selectUnprocessedAfterWait(StatusType.START);

        // when
        service.uploadStartUnprocessData();

        // then
        assertSame(response, secondQueue.pollQueue().get());
    }

    @Test
    @DisplayName("미처리된 COMPLETE상태의 데이터가 있으면 재기동시 Queue에 저장된다")
    public void uploadUnprocessCOMPLETEdataInQueue() throws Exception {
        // given
        Response response = Response.builder()
                .ownerPk(1L)
                .memberPk(2L)
                .build();

        doReturn(List.of(response)).when(initRepository).selectUnprocessedAfterWait(StatusType.COMPLETE);

        // when
        service.uploadCompleteUnprocessData();

        // then
        assertSame(response, thirdQueue.pollQueue().get());
    }
}