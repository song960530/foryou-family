package com.foryou.partyapi.api.service.impl;

import com.foryou.partyapi.api.dto.request.MatchingRequestMessage;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.entity.PartyInfo;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import com.foryou.partyapi.api.repository.PartyRepository;
import com.foryou.partyapi.api.service.PartyService;
import com.foryou.partyapi.global.error.CustomException;
import com.foryou.partyapi.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;

    @Override
    @Transactional
    public Party createMemberParty(PartyMemberReqDto partyReqDto) {
        checkSameRole(PartyRole.MEMBER, partyReqDto.getRole());
        checkExistOtt(partyReqDto.getMemberId(), partyReqDto.getOtt());

        return partyRepository.save(partyReqDto.toEntity());
    }

    @Override
    @Transactional
    public Party createOwnerParty(PartyOwnerReqDto partyReqDto) {
        checkSameRole(PartyRole.OWNER, partyReqDto.getRole());
        checkExistOtt(partyReqDto.getMemberId(), partyReqDto.getOtt());

        Party party = partyReqDto.toEntityParty();
        PartyInfo partyInfo = partyReqDto.toEntityPartyInfo();
        party.addPartyInfo(partyInfo);

        return partyRepository.save(party);
    }

    @Override
    public MatchingRequestMessage createMatchingMessage(Party party, int MatchCnt) {
        return MatchingRequestMessage.builder()
                .partyNo(party.getNo())
                .inwon(MatchCnt)
                .ott(party.getOtt())
                .role(party.getRole())
                .build();
    }

    private void checkSameRole(PartyRole expect, PartyRole actual) {
        if (expect != actual)
            throw new CustomException(ErrorCode.ROLE_NOT_MATCHED);
    }

    private void checkExistOtt(String memberId, OttType ott) {
        if (partyRepository.existOttForMember(memberId, ott))
            throw new CustomException(ErrorCode.DUPLICATE_OTT_JOIN);
    }
}
