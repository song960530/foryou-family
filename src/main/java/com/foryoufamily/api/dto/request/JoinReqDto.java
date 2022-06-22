package com.foryoufamily.api.dto.request;

import com.foryoufamily.api.entity.Member;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class JoinReqDto {

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 6, max = 12, message = "ID는 최소 6자 최대 12자 까지 입력 가능합니다")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}"
            , message = "영문 대소문자, 숫자, 특수기호가 최소 1개이상 포함된 8~20자로 입력해주세요"
    )
    private String password;

    public Member toEntity() {
        return Member.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
