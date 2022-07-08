package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MatchingServiceImplTest {

    @InjectMocks
    private MatchingServiceImpl matchingService;
    @Mock
    private MatchRepository matchRepository;

    private MatchingRequestMessage memberDto;

    @BeforeEach
    void setUp() {
        memberDto = createMember();
    }

    private MatchingRequestMessage createMember() {
        return MatchingRequestMessage.builder()
                .partyNo(1L)
                .inwon(1)
                .ott(OttType.NETFLIX)
                .role(PartyRole.MEMBER)
                .build();
    }

    @Test
    @DisplayName("생성된 No 개수가 1개")
    public void successCreateNoOne() throws Exception {
        // given
        Match match = memberDto.toEntity();

        doReturn(match).when(matchRepository).save(any(Match.class));

        // when
        List<Long> results = matchingService.createMatch(memberDto);

        // then
        assertEquals(1, results.size());
    }


    @Test
    @DisplayName("생성된 No 개수가 3개")
    public void successCreateNoThree() throws Exception {
        // given
        Match match = memberDto.toEntity();
        ReflectionTestUtils.setField(memberDto, "inwon", 3);

        doReturn(match).when(matchRepository).save(any(Match.class));

        // when
        List<Long> results = matchingService.createMatch(memberDto);

        // then
        assertEquals(3, results.size());
    }
}