package com.foryou.partyapi.api.controller;

import com.foryou.partyapi.api.dto.request.PartyReqDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.producer.KafkaPartyMatchProducer;
import com.foryou.partyapi.api.service.PartyService;
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

    @PostMapping("/party")
    public ResponseEntity<ApiResponse> requestParty(@Valid @RequestBody PartyReqDto partyReqDto) {
        Party party = partyService.createParty(partyReqDto);
        partyService.createMatchingMessage(party);
        producer.sendMessage(partyService.createMatchingMessage(party).toString());

        return ApiResponse.of(HttpStatus.OK);
    }
}
