package com.foryou.partyapi.api.dto.request;

import com.foryou.partyapi.api.entity.Party;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PartyReqDto {

    @NotBlank(message = "요청자 아이디를 입력해주세요")
    private String memberId;

    @NotNull(message = "OTT 타입을 입력해주세요")
    private OttType ott;

    @NotNull(message = "역할정보를 입력해주세요")
    private PartyRole role;

    private String id;

    private String password;

    private Integer inwon;

    public Party toEntity() {
        return Party.builder()
                .memberId(memberId)
                .role(role)
                .ott(ott)
                .build();
    }
}
