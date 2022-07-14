package com.foryou.matchingservice.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
    private Long ownerPk;
    private Long memberPk;
}
