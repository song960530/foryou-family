package com.foryou.memberapi.api.controller;

import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.service.MemberService;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MemberController.class)
@MockBean(value = JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 API 정상 호출")
    public void callJoin() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"password12!@3\"}";

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("요청 파라미터 오류 발생")
    public void argumentNotValid() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"\"}";

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("ARGUMENT_NOT_VALID"))
                .andDo(print());
    }

    @Test
    @DisplayName("중복 회원 오류 발생")
    public void duplicateMember() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"password12!@3\"}";
        doThrow(new CustomException(ErrorCode.DUPLICATE_MEMBER_ID)).when(memberService).join(any(JoinReqDto.class));

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("DUPLICATE_MEMBER_ID"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 API 정상 호출")
    public void callLogin() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"password12!@3\"}";

        doReturn(LoginResDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .type("BEARER")
                .build())
                .when(memberService)
                .login(any(LoginReqDto.class));

        // when & then
        mockMvc.perform(post(("/member/login"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.data.type").value("BEARER"))
                .andDo(print());
    }
}