package com.foryou.matchingservice.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

@Slf4j
public class ScheduledExceptionHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        CustomException customException = (CustomException) t;
        ErrorCode errorCode = customException.getErrorCode();

        log.error("ERROR CustomException : {}", errorCode);
    }
}
