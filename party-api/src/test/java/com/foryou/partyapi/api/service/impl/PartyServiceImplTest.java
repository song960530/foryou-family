package com.foryou.partyapi.api.service.impl;

import com.foryou.partyapi.api.dto.request.MatchingRequestMessage;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.dto.response.MatchingResponseMessage;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.entity.PartyInfo;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import com.foryou.partyapi.api.repository.PartyRepository;
import com.foryou.partyapi.global.error.CustomException;
import com.foryou.partyapi.global.error.ErrorCode;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PartyServiceImplTest {

    @InjectMocks
    private PartyServiceImpl service;
    @Mock
    private PartyRepository repository;

    private PartyMemberReqDto partyMember;
    private PartyOwnerReqDto partyOwner;

    @BeforeEach
    void setUp() {
        createPartyMember();
        createPartyOwner();
    }

    private void createPartyOwner() {
        partyOwner = PartyOwnerReqDto.builder()
                .memberId("owner")
                .id("owner@naver.com")
                .password("password")
                .inwon(3)
                .ott(OttType.TVING)
                .role(PartyRole.OWNER)
                .build();
    }

    private void createPartyMember() {
        partyMember = PartyMemberReqDto.builder()
                .memberId("member")
                .ott(OttType.NETFLIX)
                .role(PartyRole.MEMBER)
                .build();
    }

    @Test
    @DisplayName("파티원이 아닌데 파티원 매칭을 시도했을 때 오류 발생")
    public void partyRoleisNotMember() throws Exception {
        // given
        ReflectionTestUtils.setField(partyMember, "role", PartyRole.OWNER);

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.createMemberParty(partyMember);
        });

        // then
        assertEquals(ErrorCode.ROLE_NOT_MATCHED, customException.getErrorCode());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("이미 진행중인 파티가 있을 경우 오류 발생")
    public void existOttParty() throws Exception {
        // given
        doReturn(true).when(repository).existOttForMember(anyString(), any(OttType.class));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.createMemberParty(partyMember);
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_OTT_JOIN, customException.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("파티원으로 파티 신청 성공")
    public void successCreateMemberParty() throws Exception {
        // given
        doReturn(false).when(repository).existOttForMember(anyString(), any(OttType.class));
        doReturn(partyMember.toEntity()).when(repository).save(any(Party.class));

        // when
        Party result = service.createMemberParty(partyMember);

        // then
        assertEquals(partyMember.getMemberId(), result.getMemberId());
        assertEquals(partyMember.getRole(), result.getRole());
        assertEquals(partyMember.getOtt(), result.getOtt());
        assertEquals(false, result.getLeaveYN());
        assertNull(result.getPartyInfo());
        assertNull(result.getProfileName());
    }

    @Test
    @DisplayName("파티장으로 파티 신청 성공")
    public void successCreateOwnerParty() throws Exception {
        // given
        Party party = partyOwner.toEntityParty();
        PartyInfo partyInfo = partyOwner.toEntityPartyInfo();
        party.addPartyInfo(partyInfo);

        doReturn(false).when(repository).existOttForMember(anyString(), any(OttType.class));
        doReturn(party).when(repository).save(any(Party.class));

        // when
        Party result = service.createOwnerParty(partyOwner);

        // then
        assertEquals(partyOwner.getMemberId(), result.getMemberId());
        assertEquals(partyOwner.getRole(), result.getRole());
        assertEquals(partyOwner.getOtt(), result.getOtt());
        assertEquals(false, result.getLeaveYN());
        assertEquals(partyInfo.getInwon(), result.getPartyInfo().getInwon());
        assertEquals(partyInfo.getPartyShareId(), result.getPartyInfo().getPartyShareId());
        assertEquals(partyInfo.getPartySharePassword(), result.getPartyInfo().getPartySharePassword());
        assertEquals(partyInfo.getOttType(), result.getPartyInfo().getOttType());
    }

    @Test
    @DisplayName("Kafka 전달 용 메세지 생성_파티맴버")
    public void createMatchingMessageMember() throws Exception {
        // given
        Long fakeNo = 1L;
        Party member = partyMember.toEntity();
        ReflectionTestUtils.setField(member, "no", fakeNo);

        // when
        MatchingRequestMessage result = service.createMatchingMessage(member, 1);

        // then
        assertEquals(member.getNo(), result.getPartyNo());
        assertEquals(member.getOtt(), result.getOtt());
        assertEquals(member.getRole(), result.getRole());
        assertEquals(1, result.getInwon());
    }

    @Test
    @DisplayName("Kafka 전달 용 메세지 생성_파티장")
    public void createMatchingMessageOwner() throws Exception {
        // given
        Long fakeNo = 1L;
        Party owner = partyOwner.toEntityParty();
        owner.addPartyInfo(partyOwner.toEntityPartyInfo());
        ReflectionTestUtils.setField(owner, "no", fakeNo);
        ReflectionTestUtils.setField(owner.getPartyInfo(), "no", fakeNo);

        // when
        MatchingRequestMessage result = service.createMatchingMessage(owner, owner.getPartyInfo().getInwon());

        // then
        assertEquals(owner.getNo(), result.getPartyNo());
        assertEquals(owner.getOtt(), result.getOtt());
        assertEquals(owner.getRole(), result.getRole());
        assertEquals(owner.getPartyInfo().getInwon(), result.getInwon());
    }

    @Test
    @DisplayName("파티 매칭이되어 마무리 매칭 중 Owner의 No값이 잘못 넘어와서 Party 조회를 실패하였을 때")
    public void incorrectOwnerNo() throws Exception {
        // given
        MatchingResponseMessage response = MatchingResponseMessage.builder()
                .ownerNo(1L)
                .memberNo(2L)
                .ott(OttType.NETFLIX)
                .build();

        doReturn(Optional.empty()).when(repository).findById(anyLong());

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            service.finishMatch(response);
        });

        // then
        assertEquals(ErrorCode.NOT_MATCHED_PARTY_NO, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }


    @Test
    @DisplayName("파티매칭이 완료되어 정상적으로 마무리 매칭")
    public void successMatching() throws Exception {
        // given
        MatchingResponseMessage response = MatchingResponseMessage.builder()
                .ownerNo(1L)
                .memberNo(2L)
                .ott(OttType.NETFLIX)
                .build();

        doReturn(Optional.of(partyOwner.toEntityParty())).when(repository).findById(anyLong());

        // when
        service.finishMatch(response);

        // then
    }
}
