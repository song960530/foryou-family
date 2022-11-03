package com.foryou.partyapi.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.entity.PartyInfo;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class PartyOwnerReqDto {

    @JsonIgnore
    private String memberId;

    @NotNull(message = "OTT 타입을 확인해주세요")
    private OttType ott;

    @NotNull(message = "역할정보를 확인해주세요")
    private PartyRole role;

    @NotBlank(message = "파티의 ID를 입력해주세요")
    private String id;

    @NotBlank(message = "파티의 비밀번호를 입력해주세요")
    private String password;

    @Min(
            message = "모집인원은 최소 1명 이상이여야 합니다"
            , value = 1
    )
    @Max(
            message = "모집인원은 최대 3명까지 가능합니다"
            , value = 3
    )
    private int inwon;

    public Party toEntityParty() {
        return Party.builder()
                .memberId(memberId)
                .role(role)
                .ott(ott)
                .leaveYN(false)
                .build();
    }

    public PartyInfo toEntityPartyInfo() {
        return PartyInfo.builder()
                .inwon(inwon)
                .ottType(ott)
                .partyShareId(id)
                .partySharePassword(password)
                .build();
    }
}
