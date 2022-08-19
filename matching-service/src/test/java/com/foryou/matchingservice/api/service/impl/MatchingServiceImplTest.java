package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.queue.first.*;
import com.foryou.matchingservice.api.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MatchingServiceImplTest {

    private MatchingServiceImpl matchingService;
    @Mock
    private MatchRepository matchRepository;
    @Spy
    private Netflix netflix;
    @Spy
    private Tving tving;
    @Spy
    private Disney disney;
    @Spy
    private Watcha watcha;
    @Spy
    private Wavve wavve;

    @BeforeEach
    void setUp() {
        this.matchingService = new MatchingServiceImpl(matchRepository, netflix, tving, disney, watcha, wavve);
    }

    private MatchingRequestMessage createOwner(OttType type) {
        return MatchingRequestMessage.builder()
                .partyNo(2L)
                .inwon(3)
                .ott(type)
                .role(PartyRole.OWNER)
                .build();
    }

    private MatchingRequestMessage createMember(OttType type) {
        return MatchingRequestMessage.builder()
                .partyNo(1L)
                .inwon(1)
                .ott(type)
                .role(PartyRole.MEMBER)
                .build();
    }

    @Test
    @DisplayName("생성된 No 개수가 1개")
    public void successCreateNoOne() throws Exception {
        // given
        MatchingRequestMessage memberDto = createMember(OttType.NETFLIX);
        Match match = memberDto.toEntity();

        doReturn(match).when(matchRepository).save(any(Match.class));

        // when
        List<Match> results = matchingService.createMatch(memberDto);

        // then
        assertEquals(1, results.size());
    }


    @Test
    @DisplayName("생성된 No 개수가 3개")
    public void successCreateNoThree() throws Exception {
        // given
        MatchingRequestMessage memberDto = createMember(OttType.NETFLIX);
        Match match = memberDto.toEntity();
        ReflectionTestUtils.setField(memberDto, "inwon", 3);

        doReturn(match).when(matchRepository).save(any(Match.class));

        // when
        List<Match> results = matchingService.createMatch(memberDto);

        // then
        assertEquals(3, results.size());
    }

    @Test
    @DisplayName("Netflix는 Netflix Queue로 offer한다")
    public void offerWhenNetflix() throws Exception {
        // given
        Match member = createMember(OttType.NETFLIX).toEntity();
        Match owner = createOwner(OttType.NETFLIX).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
        assertEquals(1, netflix.memberQueueSize());
        assertEquals(1, netflix.ownerQueueSize());
    }

    @Test
    @DisplayName("Tving은 Tving Queue로 offer한다")
    public void offerWhenTving() throws Exception {
        // given
        Match member = createMember(OttType.TVING).toEntity();
        Match owner = createOwner(OttType.TVING).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
        assertEquals(1, tving.memberQueueSize());
        assertEquals(1, tving.ownerQueueSize());
    }

    @Test
    @DisplayName("Disney는 Disney Queue로 offer한다")
    public void offerWhenDisney() throws Exception {
        // given
        Match member = createMember(OttType.DISNEY_PLUS).toEntity();
        Match owner = createOwner(OttType.DISNEY_PLUS).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
        assertEquals(1, disney.memberQueueSize());
        assertEquals(1, disney.ownerQueueSize());
    }

    @Test
    @DisplayName("Watcha는 Watcha Queue로 offer한다")
    public void offerWhenWatcha() throws Exception {
        // given
        Match member = createMember(OttType.WATCHA).toEntity();
        Match owner = createOwner(OttType.WATCHA).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
        assertEquals(1, watcha.memberQueueSize());
        assertEquals(1, watcha.ownerQueueSize());
    }

    @Test
    @DisplayName("Wavve은 Wavve Queue로 offer한다")
    public void offerWhenWavve() throws Exception {
        // given
        Match member = createMember(OttType.WAVVE).toEntity();
        Match owner = createOwner(OttType.WAVVE).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
        assertEquals(1, wavve.memberQueueSize());
        assertEquals(1, wavve.ownerQueueSize());
    }
}