package com.foryou.matchingservice.api.service.impl;

import com.foryou.matchingservice.api.dto.request.MatchingRequestMessage;
import com.foryou.matchingservice.api.entity.Match;
import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import com.foryou.matchingservice.api.repository.MatchRepository;
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
    @DisplayName("Member일때 MemberQueue로 Offer")
    public void offerWhenMember() throws Exception {
        // given
        Match member = createMember(OttType.NETFLIX).toEntity();
        Match owner = createOwner(OttType.NETFLIX).toEntity();

        // when
        matchingService.offerQueue(List.of(member, owner));

        // then
    }
}