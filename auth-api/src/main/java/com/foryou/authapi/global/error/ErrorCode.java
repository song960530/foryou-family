package com.foryou.authapi.global.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST: 파라미터가 잘못 넘어옴 */
    ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 정보가 잘못되었습니다"),
    NOT_VALID_TOKEN_FORM(HttpStatus.BAD_REQUEST, "토큰값이 잘못되었습니다"),
    NOT_VALID_TOKEN_VALUE(HttpStatus.BAD_REQUEST, "토큰값이 잘못되었습니다"),
    NOt_EXIST_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레쉬 토큰이 존재하지 않습니다"),

    /* 401 UNAUTHORIZED: 리소스에 유효한 인증 자격이 증명되지 않음*/
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다"),

    /* 404 요청에 대한 리소스를 찾지 못하였거나 없음*/
    NOT_EXIST_TOKEN(HttpStatus.NOT_FOUND, "일치하는 토큰정보가 없습니다"),

    /* 409 CONFLICT: 중복 Resource 존재 */

    /* 500 INTERNAL_SERVER_ERROR: 지정하지  않은 오류 발생 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
