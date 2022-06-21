package com.foryoufamily.global.error;

import com.foryoufamily.global.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiErrorResponse> customException(CustomException e) {

        log.error("ERROR CustomException : {}", e.getErrorCode());
        return ApiErrorResponse.of(e.getErrorCode());
    }

}
