package com.foryou.partyapi.api.controller;

import com.foryou.partyapi.api.dto.request.PartyReqDto;
import com.foryou.partyapi.api.producer.KafkaPartyRequestProducer;
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
    private final KafkaPartyRequestProducer producer;

    @PostMapping("/party")
    public ResponseEntity<ApiResponse> requestParty(@Valid @RequestBody PartyReqDto partyReqDto) {
        producer.sendMessage(partyReqDto.toString());

        return ApiResponse.of(HttpStatus.OK);
    }
}
