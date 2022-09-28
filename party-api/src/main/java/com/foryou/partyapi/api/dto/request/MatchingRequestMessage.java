package com.foryou.partyapi.api.dto.request;

import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
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
    private Long paymentNo;
    private String memberId;
    private Integer inwon;
    private OttType ott;
    private PartyRole role;
}
