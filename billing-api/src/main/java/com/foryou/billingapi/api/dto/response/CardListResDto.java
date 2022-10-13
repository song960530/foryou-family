package com.foryou.billingapi.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CardListResDto {

    private int count;
    private String memberId;
    List<CardList> paymentCardList;

    @Data
    @Builder
    public static class CardList {

        private Long paymentNo;
        private String cardNum4Digit;
        private LocalDateTime createDate;
    }
}
