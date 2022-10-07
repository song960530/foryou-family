package com.foryou.memberapi.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private int status;
    private Object data;

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
