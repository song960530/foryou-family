package com.foryou.billingapi.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST: 파라미터가 잘못 넘어옴 */
    CARD_REGISTRATION_FAILED(HttpStatus.BAD_REQUEST, "결제카드 등록을 실패하였습니다."),
    ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 정보가 잘못되었습니다"),
    CIPHER_DECRYPT_ERROR(HttpStatus.BAD_REQUEST, "AES256 복호화를 실패하였습니다")

    /* 401 UNAUTHORIZED: 리소스에 유효한 인증 자격이 증명되지 않음*/,
    NOT_VALID_IAMPORT_KEY(HttpStatus.UNAUTHORIZED, "Key 정보가 잘못되었습니다. 인증에 실패하였습니다"),
    NOT_MATCH_MEMBERID(HttpStatus.UNAUTHORIZED, "결제 요청 정보가 잘못되었습니다")

    /* 404 요청에 대한 리소스를 찾지 못하였거나 없음*/,
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다")

    /* 409 CONFLICT: 중복 Resource 존재 */

    /* 500 INTERNAL_SERVER_ERROR: 지정하지  않은 오류 발생 */,
    IAMPORT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "아임포트 서버와 통신을 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다"),
    CIPHER_INIT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 초기화를 실패하였습니다"),
    CIPHER_ENCRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 암호화를 실패하였습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
