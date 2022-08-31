package com.foryou.billingapi.global.response;

import com.foryou.billingapi.global.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;

@Getter
@Builder
public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final Object data;

    public static final ApiErrorResponse of(final ErrorCode errorCode) {
        return ApiErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .data(Collections.emptyList())
                .build()
                ;
    }

    public static final ApiErrorResponse of(final ErrorCode errorCode, final Object data) {
        return ApiErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .data(data)
                .build()
                ;
    }
}
