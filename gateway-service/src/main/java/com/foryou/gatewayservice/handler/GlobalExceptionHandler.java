package com.foryou.gatewayservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.gatewayservice.exception.ApiErrorResponse;
import com.foryou.gatewayservice.exception.CustomException;
import com.foryou.gatewayservice.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ApiErrorResponse errorResponse = Optional.of(ex)
                .map(e -> (CustomException) e)
                .map(CustomException::getErrorCode)
                .map(ApiErrorResponse::of)
                .orElse(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));

        String errorBody = "";
        try {
            errorBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException In GlobalExceptionHandler");
        }

        DataBuffer buffer = Optional.of(errorBody)
                .map(body -> body.getBytes(StandardCharsets.UTF_8))
                .map(bytes -> exchange.getResponse().bufferFactory().wrap(bytes))
                .get();

        log.error("GateWay ERROR >>>>>> {}", errorResponse.getMessage());

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorResponse.getStatus()));
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
