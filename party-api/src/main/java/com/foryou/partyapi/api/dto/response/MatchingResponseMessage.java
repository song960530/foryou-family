package com.foryou.partyapi.api.dto.response;

import com.foryou.partyapi.api.enums.OttType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MatchingResponseMessage {
    private Long ownerNo;
    private Long memberNo;
    private OttType ott;
}