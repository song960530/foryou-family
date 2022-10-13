package com.foryou.partyapi.api.dto.response;

import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyInfoResDto {

    private OttType ott;
    private int inwon;
    private String partyId;
    private String partyPassword;
    private List<PartyMember> memberList;

    @Data
    @Builder
    public static class PartyMember {
        private String profile;
        private PartyRole role;
    }
}
