package com.foryou.partyapi.global.error;

import com.foryou.partyapi.global.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> exception(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiErrorResponse errorResponse = ApiErrorResponse.of(errorCode);

        log.error("ERROR CustomException : {}", errorCode);
        e.printStackTrace();

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> customException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiErrorResponse errorResponse = ApiErrorResponse.of(errorCode);

        log.error("ERROR CustomException : {}", errorCode);

        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.ARGUMENT_NOT_VALID;
        ApiErrorResponse errorResponse = ApiErrorResponse.of(errorCode, createNotValidMap(ex));

        return new ResponseEntity<>(errorResponse, status);
    }

    private Map<Object, Object> createNotValidMap(MethodArgumentNotValidException ex) {
        Map<Object, Object> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });

        return errors;
    }
}
