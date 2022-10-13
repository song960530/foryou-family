package com.foryou.partyapi.api.dto.response;

import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPartyResDto {
    private OttType ott;
    private PartyRole role;
    private String status;
}
