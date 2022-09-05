package com.foryou.partyapi.api.dto.request;

import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class PartyMemberReqDto {

    private String memberId;

    @NotNull(message = "OTT 타입을 확인해주세요")
    private OttType ott;

    @NotNull(message = "역할정보를 확인해주세요")
    private PartyRole role;

    @NotNull(message = "결제카드 정보를 확인해주세요")
    private Long paymentNo;

    public Party toEntity() {
        return Party.builder()
                .memberId(memberId)
                .role(role)
                .ott(ott)
                .leaveYN(false)
                .build();
    }
}
