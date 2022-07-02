package com.foryou.partyapi.api.service;

import com.foryou.partyapi.api.dto.request.KafKaPartyMatchReqDto;
import com.foryou.partyapi.api.dto.request.PartyReqDto;
import com.foryou.partyapi.api.entity.Party;

public interface PartyService {
    Party createParty(PartyReqDto partyReqDto);

    KafKaPartyMatchReqDto createMatchingMessage(Party party);
}
