package com.foryou.matchingservice.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST: 파라미터가 잘못 넘어옴 */
    NOT_EXIST_WAIT_PEOPLE(HttpStatus.BAD_REQUEST, "파티 신청자가 존재하지 않습니다"),
    NOT_EXIST_START_PEOPLE(HttpStatus.BAD_REQUEST, "파티 신청자가 존재하지 않습니다"),

    /* 500 INTERNAL_SERVER_ERROR: 지정하지 않은 오류 발생 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
