package com.foryou.partyapi.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST: 파라미터가 잘못 넘어옴 */
    ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "요청 정보가 잘못되었습니다"),
    NOT_MATCHED_MEMBER_ID(HttpStatus.BAD_REQUEST, "요청자 정보가 잘못되었습니다"),
    NOT_MATCHED_PARTY_NO(HttpStatus.BAD_REQUEST, "사용자 조회를 실패하였습니다"),
    NOT_EXIST_PARTY(HttpStatus.BAD_REQUEST, "존재하지 않는 파티입니다"),

    /* 401 UNAUTHORIZED: 리소스에 유효한 인증 자격이 증명되지 않음*/
    ROLE_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "파티원/파티장 역할정보가 잘못되었습니다"),

    /* 409 CONFLICT: 중복 Resource 존재 */
    DUPLICATE_OTT_JOIN(HttpStatus.CONFLICT, "이미 가입한 ott파티가 존재합니다"),

    /* 500 INTERNAL_SERVER_ERROR: 지정하지  않은 오류 발생 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다"),
    CIPHER_INIT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 초기화를 실패하였습니다"),
    CIPHER_ENCRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 암호화를 실패하였습니다"),
    CIPHER_DECRYPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AES256 복호화를 실패하였습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
