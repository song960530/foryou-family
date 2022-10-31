package com.foryou.authapi.api.controller;

import com.foryou.authapi.api.service.AuthService;
import com.foryou.authapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
    }
    
    @PostMapping("/auth/{memberId}")
    public ResponseEntity<ApiResponse> createToken(@PathVariable String memberId) {

        return ApiResponse.of(HttpStatus.OK, service.createOrUpdateToken(memberId));
    }

    @PatchMapping("/reAuth/{memberId}")
    public ResponseEntity<ApiResponse> reCreateToken(@PathVariable String memberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        return ApiResponse.of(HttpStatus.OK, service.reCreateToken(memberId, httpServletRequest, httpServletResponse));
    }
}
