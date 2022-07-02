package com.foryou.partyapi.api.dto.request;

import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafKaPartyMatchReqDto {
    private Long partyNo;
    private Long partyInfoNo;
    private Integer inwon;
    private OttType ott;
    private PartyRole role;
}
