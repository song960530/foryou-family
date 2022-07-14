package com.foryou.matchingservice.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Response {
    private Long ownerPk;
    private Long memberPk;
}
