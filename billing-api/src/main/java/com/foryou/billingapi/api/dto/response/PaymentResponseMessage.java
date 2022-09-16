package com.foryou.billingapi.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseMessage {

    private String memberId;
    private Long partyNo;
    private Long paymentNo;
    private boolean success;
}
