package com.foryou.partyapi.api.controller;

import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.service.PartyService;
import com.foryou.partyapi.api.service.kafka.producer.KafkaProducer;
import com.foryou.partyapi.global.constants.Constants;
import com.foryou.partyapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final KafkaProducer producer;

    @PostMapping("/party/{memberId}/member")
    public ResponseEntity<ApiResponse> requestPartyMember(
            @PathVariable String memberId
            , @Valid @RequestBody PartyMemberReqDto partyReqDto
    ) {
        partyReqDto.setMemberId(memberId);
        Party party = partyService.createMemberParty(partyReqDto);

        producer.sendMessage(
                Constants.KAFKA_TOPIC_PARTY
                , partyService.createMatchingMessage(party, 1, partyReqDto.getPaymentNo())
        );

        return ApiResponse.of(HttpStatus.OK);
    }

    @PostMapping("/party/{memberId}/owner")
    public ResponseEntity<ApiResponse> requestPartyOwner(
            @PathVariable String memberId
            , @Valid @RequestBody PartyOwnerReqDto partyReqDto
    ) {
        partyReqDto.setMemberId(memberId);
        Party party = partyService.createOwnerParty(partyReqDto);

        producer.sendMessage(
                Constants.KAFKA_TOPIC_PARTY
                , partyService.createMatchingMessage(party, party.getPartyInfo().getInwon(), 0L)
        );

        return ApiResponse.of(HttpStatus.OK);
    }

    @GetMapping("/myparty/{memberId}")
    public ResponseEntity<ApiResponse> myParty(@PathVariable String memberId) {
        return ApiResponse.of(HttpStatus.OK, partyService.myParty(memberId));
    }

    @GetMapping("/myparty/{memberId}/parties/{partyNo}")
    public ResponseEntity<ApiResponse> partyInfo(@PathVariable String memberId, @PathVariable Long partyNo) {
        return ApiResponse.of(HttpStatus.OK, partyService.partyInfo(partyNo));
    }
}
