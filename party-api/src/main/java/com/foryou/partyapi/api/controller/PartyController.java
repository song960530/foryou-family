package com.foryou.partyapi.api.controller;

import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.service.PartyService;
import com.foryou.partyapi.api.service.kafka.KafkaPartyMatchProducer;
import com.foryou.partyapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final KafkaPartyMatchProducer producer;

    @PostMapping("/party/member")
    public ResponseEntity<ApiResponse> requestPartyMember(@Valid @RequestBody PartyMemberReqDto partyReqDto) {
        Party party = partyService.createMemberParty(partyReqDto);
        producer.sendMessage(partyService.createMatchingMessage(party));

        return ApiResponse.of(HttpStatus.OK);
    }

    @PostMapping("/party/owner")
    public ResponseEntity<ApiResponse> requestPartyOwner(@Valid @RequestBody PartyOwnerReqDto partyReqDto) {
        Party party = partyService.createOwnerParty(partyReqDto);
        producer.sendMessage(partyService.createMatchingMessage(party));

        return ApiResponse.of(HttpStatus.OK);
    }
}
