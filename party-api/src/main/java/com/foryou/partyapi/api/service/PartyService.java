package com.foryou.partyapi.api.service;

import com.foryou.partyapi.api.dto.request.MatchingRequestMessage;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.entity.Party;

public interface PartyService {
    Party createMemberParty(PartyMemberReqDto partyReqDto);

    Party createOwnerParty(PartyOwnerReqDto partyReqDto);

    MatchingRequestMessage createMatchingMessage(Party party, int MatchCnt);
}
