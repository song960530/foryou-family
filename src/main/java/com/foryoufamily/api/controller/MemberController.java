package com.foryoufamily.api.controller;

import com.foryoufamily.api.dto.request.JoinReqDto;
import com.foryoufamily.api.service.MemberService;
import com.foryoufamily.global.response.ApiResponse;
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

    @PostMapping("/user/join")
    public ResponseEntity<ApiResponse> join(@Valid @RequestBody JoinReqDto joinDto) {

        memberService.join(joinDto);

        return ApiResponse.of(HttpStatus.CREATED);
    }
}
