package com.foryou.billingapi.api.dto.request;

import com.foryou.billingapi.api.enums.OttType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {

    private String memberId;
    private Long partyNo;
    private Long paymentNo;
    private OttType ott;
}
