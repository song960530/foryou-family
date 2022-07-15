package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.response.Response;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.enums.StatusType;
import com.foryou.matchingservice.api.repository.MatchRepository;
import com.foryou.matchingservice.api.service.kafka.KafkaMatchResultProducer;
import com.foryou.matchingservice.global.error.CustomException;
import com.foryou.matchingservice.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ScheduledServiceImplTest {

    @InjectMocks
    private ScheduledServiceImpl service;
    @Mock
    private MatchRepository repository;
    @Mock
    private KafkaMatchResultProducer producer;
    private Match owner;
    private Match member;

    @BeforeEach
    void setUp() {
        owner = Match.builder()
                .partyNo(1L)
                .role(PartyRole.OWNER)
                .ott(OttType.NETFLIX)
                .build();

        member = Match.builder()
                .partyNo(2L)
                .role(PartyRole.MEMBER)
                .ott(OttType.NETFLIX)
                .build();

        ReflectionTestUtils.setField(owner, "no", 1L);
        ReflectionTestUtils.setField(member, "no", 2L);
    }

    @Test
    @DisplayName("상태가 WAIT가 아닐 경우 오류 발생")
    public void ExceptionWhenNotWait() throws Exception {
        // given
        doReturn(Optional.empty()).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.firstMatchJob(owner.getNo(), member.getNo());
        });

        // then
        assertEquals(ErrorCode.NOT_EXIST_WAIT_PEOPLE, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("firstSmatchJob 정상동작_Owner")
    public void successFirstmatchJob() throws Exception {
        // given
        doReturn(Optional.of(owner)).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        Response result = service.firstMatchJob(owner.getNo(), member.getNo());

        // then
        assertNotNull(result);
        assertEquals(owner.getNo(), result.getOwnerPk());
    }


    @Test
    @DisplayName("상태가 START가 아닐 경우 오류 발생")
    public void ExceptionWhenNotStart() throws Exception {
        // given
        doReturn(Optional.empty()).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.secondMatchJob(owner.getNo(), member.getNo());
        });

        // then
        assertEquals(ErrorCode.NOT_EXIST_START_PEOPLE, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("secondMatchJob 정상동작_Owner")
    public void successSecondMatchJob() throws Exception {
        // given
        doReturn(Optional.of(owner)).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        Response result = service.secondMatchJob(owner.getNo(), member.getNo());

        // then
        assertNotNull(result);
        assertEquals(owner.getNo(), result.getOwnerPk());
    }

    @Test
    @DisplayName("상태가 COMPLETE가 아닐 경우 오류 발생")
    public void ExceptionWhenNotComplete() throws Exception {
        // given
        doReturn(Optional.empty()).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.thirdMatchJob(owner.getNo(), member.getNo());
        });

        // then
        assertEquals(ErrorCode.NOT_EXIST_COMPLETE_PEOPLE, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("thirdMatchJob 정상동작_Owner")
    public void successThirdMatchJob() throws Exception {
        // given
        doReturn(Optional.of(owner)).when(repository).findByNoAndStatus(anyLong(), any(StatusType.class));

        // when
        service.thirdMatchJob(owner.getNo(), member.getNo());

        // then
    }
}