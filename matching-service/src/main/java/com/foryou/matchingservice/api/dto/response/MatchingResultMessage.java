package com.foryou.matchingservice.api.dto.response;

import com.foryou.matchingservice.api.enums.OttType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MatchingResultMessage {
    private Long ownerNo;
    private Long memberNo;
    private OttType ott;
}
