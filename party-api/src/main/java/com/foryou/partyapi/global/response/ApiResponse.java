package com.foryou.partyapi.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

@Getter
@Builder
public class ApiResponse {

    private final int status;
    private final Object data;

    public static final ResponseEntity<ApiResponse> of(final HttpStatus status, final Object data) {
        return ResponseEntity
                .status(status.value())
                .body(ApiResponse.builder()
                        .status(status.value())
                        .data(data)
                        .build()
                );
    }

    public static final ResponseEntity<ApiResponse> of(final HttpStatus status) {
        return ResponseEntity
                .status(status.value())
                .body(ApiResponse.builder()
                        .status(status.value())
                        .data(Collections.emptyList())
                        .build()
                );
    }
}
