package com.foryou.billingapi.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDto {
    @NotBlank(message = "카드 번호를 입력해주세요")
    private String cardNum;

    @NotBlank(message = "만료일을 입력해주세요")
    private String expiredDate;

    @NotBlank(message = "생년월일을 입력해주세요")
    private String birthDate;

    @NotBlank(message = "카드 비밀번호 앞 2자리를 입력해주세요")
    private String pwd2digit;
}
