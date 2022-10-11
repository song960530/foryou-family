package com.foryou.partyapi.api.service;

import com.foryou.partyapi.api.dto.request.MatchingRequestMessage;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.dto.response.MatchingResponseMessage;
import com.foryou.partyapi.api.dto.response.MyPartyResDto;
import com.foryou.partyapi.api.dto.response.PartyInfoResDto;
import com.foryou.partyapi.api.entity.Party;

import java.util.List;

public interface PartyService {
    Party createMemberParty(PartyMemberReqDto partyReqDto);

    Party createOwnerParty(PartyOwnerReqDto partyReqDto);

    MatchingRequestMessage createMatchingMessage(Party party, int MatchCnt, Long paymentNo);

    void finishMatch(MatchingResponseMessage response);

    PartyInfoResDto partyInfo(Long partyNo);

    List<MyPartyResDto> myParty(String memberId);
}
