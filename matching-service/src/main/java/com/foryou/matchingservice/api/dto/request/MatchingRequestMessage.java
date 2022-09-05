package com.foryou.matchingservice.api.dto.request;

import com.foryou.matchingservice.api.entity.Match;
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
    private String memberId;
    private Long paymentNo;
    private Long partyNo;
    private Integer inwon;
    private OttType ott;
    private PartyRole role;

    public Match toEntity() {
        return Match.builder()
                .memberId(memberId)
                .paymentNo(paymentNo)
                .partyNo(partyNo)
                .ott(ott)
                .role(role)
                .build();
    }
}
