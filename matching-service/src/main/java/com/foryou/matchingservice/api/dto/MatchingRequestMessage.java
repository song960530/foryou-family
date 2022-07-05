package com.foryou.matchingservice.api.dto;

import com.foryou.matchingservice.api.enums.OttType;
import com.foryou.matchingservice.api.enums.PartyRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequestMessage {
    private Long partyNo;
    private Long partyInfoNo;
    private Integer inwon;
    private OttType ott;
    private PartyRole role;
}
