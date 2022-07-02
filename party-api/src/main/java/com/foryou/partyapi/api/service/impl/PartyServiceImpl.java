package com.foryou.partyapi.api.service.impl;

import com.foryou.partyapi.api.dto.request.KafKaPartyMatchReqDto;
import com.foryou.partyapi.api.dto.request.PartyReqDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.entity.PartyInfo;
import com.foryou.partyapi.api.enums.PartyRole;
import com.foryou.partyapi.api.repository.PartyRepository;
import com.foryou.partyapi.api.service.PartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;

    @Override
    @Transactional
    public Party createParty(PartyReqDto partyReqDto) {
        return partyRepository.save(createPartyEntity(partyReqDto));
    }

    @Override
    public KafKaPartyMatchReqDto createMatchingMessage(Party party) {
        if (party.getRole() == PartyRole.OWNER) {
            return KafKaPartyMatchReqDto.builder()
                    .partyNo(party.getNo())
                    .partyInfoNo(party.getPartyInfo().getNo())
                    .inwon(party.getPartyInfo().getInwon())
                    .ott(party.getOtt())
                    .role(party.getRole())
                    .build();
        } else {
            return KafKaPartyMatchReqDto.builder()
                    .partyNo(party.getNo())
                    .ott(party.getOtt())
                    .role(party.getRole())
                    .build();
        }
    }

    private Party createPartyEntity(PartyReqDto partyReqDto) {
        return Optional.of(partyReqDto)
                .filter(dto -> dto.getRole() == PartyRole.OWNER)
                .map(dto -> dto.toEntity())
                .map(party -> {
                    party.addPartyInfo(
                            PartyInfo.builder()
                                    .ottType(partyReqDto.getOtt())
                                    .inwon(partyReqDto.getInwon())
                                    .partyShareId(partyReqDto.getId())
                                    .partySharePassword(partyReqDto.getPassword())
                                    .build());

                    return party;
                })
                .orElseGet(() -> partyReqDto.toEntity());
    }
}
