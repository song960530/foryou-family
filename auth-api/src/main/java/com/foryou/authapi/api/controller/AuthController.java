package com.foryou.authapi.api.controller;

import com.foryou.authapi.api.service.AuthService;
import com.foryou.authapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/auth/{memberId}")
    public ResponseEntity<ApiResponse> createToken(@PathVariable String memberId) {

        return ApiResponse.of(HttpStatus.OK, service.createOrUpdateToken(memberId));
    }
}
