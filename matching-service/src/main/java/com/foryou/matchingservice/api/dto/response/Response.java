package com.foryou.matchingservice.api.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private Long ownerPk;
    private Long memberPk;

    @QueryProjection
    public Response(Long ownerPk, Long memberPk) {
        this.ownerPk = ownerPk;
        this.memberPk = memberPk;
    }
}
