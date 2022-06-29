package com.foryou.memberapi.api.controller;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.service.MemberService;
import com.foryou.memberapi.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<ApiResponse> join(@Valid @RequestBody JoinReqDto joinDto) {
        memberService.join(joinDto);

        return ApiResponse.of(HttpStatus.CREATED);
    }

    @PostMapping("/member/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        return ApiResponse.of(HttpStatus.OK, memberService.login(loginReqDto));
    }
}
