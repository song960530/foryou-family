package com.foryoufamily.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 409 CONFLICT: 중복 Resource 존재 */
    DUPLICATE_USERID(CONFLICT, "존재하는 아이디입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
