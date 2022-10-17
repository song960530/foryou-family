package com.foryou.memberapi.api.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.memberapi.api.dto.request.JoinReqDto;
import com.foryou.memberapi.api.dto.request.LoginReqDto;
import com.foryou.memberapi.api.dto.response.LoginResDto;
import com.foryou.memberapi.api.service.MemberService;
import com.foryou.memberapi.global.error.CustomException;
import com.foryou.memberapi.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(value = MemberController.class)
@MockBean(value = JpaMetamodelMappingContext.class)
class MemberControllerTest {

    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("회원가입")
    public void joinSuccess() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"password123!@\"}";

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("member-create"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Member-Api")
                                        .summary("회원가입")
                                        .description("사용자 정보를 생성한다")
                                        .requestSchema(schema("JoinReqDto"))
                                        .responseSchema(schema("ApiResponse"))
                                        .requestFields(
                                                fieldWithPath("memberId").description("회원 아이디")
                                                , fieldWithPath("password").description("비밀번호")
                                        )
                                        .responseFields(
                                                fieldWithPath("status").description("응답 코드")
                                                , fieldWithPath("data").description("응답 데이터")
                                        )
                                        .build()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("중복 회원가입")
    public void existMemberid() throws Exception {
        // given
        String content = "{\"memberId\":\"test12345\",\"password\":\"password123!@\"}";

        doThrow(new CustomException(ErrorCode.DUPLICATE_MEMBER_ID)).when(memberService).join(any(JoinReqDto.class));

        // when & then
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.CONFLICT.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.DUPLICATE_MEMBER_ID.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.DUPLICATE_MEMBER_ID.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("member-create-fail"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("Member-Api")
                                                .requestSchema(schema("JoinReqDto"))
                                                .responseSchema(schema("ApiErrorResponse"))
                                                .requestFields(
                                                        fieldWithPath("memberId").description("회원 아이디")
                                                        , fieldWithPath("password").description("비밀번호")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").description("응답 코드")
                                                        , fieldWithPath("error").description("응답 코드 명")
                                                        , fieldWithPath("code").description("오류 코드")
                                                        , fieldWithPath("message").description("오류 코드 메세지")
                                                        , fieldWithPath("data").description("응답 데이터")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인 성공")
    public void successLogin() throws Exception {
        // given
        String sampleJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtZW1iZXJJZCI6InRlc3QxMjM0NSJ9.n-3gy_VEip4twMfKWBswHdmPPnSXf1-hP8P-9zIsvx8";
        String body = new ObjectMapper().writeValueAsString(LoginReqDto.builder().memberId("test12345").password("password123!@'").build());
        LoginResDto response = LoginResDto.builder().accessToken(sampleJwt).refreshToken("httponly").type("BEARER").build();


        doReturn(response).when(memberService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.data.accessToken", is(sampleJwt)))
                .andExpect(jsonPath("$.data.refreshToken", is("httponly")))
                .andExpect(jsonPath("$.data.type", is("BEARER")))
                .andDo(print())
                .andDo(document("member-login"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("Member-Api")
                                                .summary("로그인")
                                                .description("사용자 로그인을 시도한다<br>로그인 시도 시 인증서버를 통해 토큰을 발급받는다<br>Access-Token은 Body에, Refresh-Token은 cookie에 저장")
                                                .requestSchema(schema("LoginReqDto"))
                                                .responseSchema(schema("ApiResponse"))
                                                .requestFields(
                                                        fieldWithPath("memberId").description("회원 아이디")
                                                        , fieldWithPath("password").description("비밀번호")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").description("응답 코드")
                                                        , fieldWithPath("data").description("응답 데이터")
                                                        , fieldWithPath("data.accessToken").description("액세스토큰")
                                                        , fieldWithPath("data.refreshToken").description("리프레쉬토큰 cookie에 httponly로 저장")
                                                        , fieldWithPath("data.type").description("인증타입")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그인 실패_아이디없음")
    public void notExistMember() throws Exception {
        // given
        String body = new ObjectMapper().writeValueAsString(LoginReqDto.builder().memberId("noMember").password("password123!@'").build());

        doThrow(new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID)).when(memberService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.NOT_EXIST_MEMBER_ID.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.NOT_EXIST_MEMBER_ID.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("not-exist-member-id"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("Member-Api")
                                                .requestSchema(schema("LoginReqDto"))
                                                .responseSchema(schema("ApiResponse"))
                                                .requestFields(
                                                        fieldWithPath("memberId").description("회원 아이디")
                                                        , fieldWithPath("password").description("비밀번호")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").description("응답 코드")
                                                        , fieldWithPath("error").description("응답 코드 명")
                                                        , fieldWithPath("code").description("오류 코드")
                                                        , fieldWithPath("message").description("오류 코드 메세지")
                                                        , fieldWithPath("data").description("응답 데이터")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Test
    @DisplayName("비밀번호 틀림")
    public void notMatchedPassword() throws Exception {
        // given
        String body = new ObjectMapper().writeValueAsString(LoginReqDto.builder().memberId("test12345").password("failpass123!@#").build());

        doThrow(new CustomException(ErrorCode.NOT_MATCHED_PASSWORD)).when(memberService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.NOT_MATCHED_PASSWORD.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.NOT_MATCHED_PASSWORD.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("not_matched_password"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("Member-Api")
                                                .requestSchema(schema("LoginReqDto"))
                                                .responseSchema(schema("ApiResponse"))
                                                .requestFields(
                                                        fieldWithPath("memberId").description("회원 아이디")
                                                        , fieldWithPath("password").description("비밀번호")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").description("응답 코드")
                                                        , fieldWithPath("error").description("응답 코드 명")
                                                        , fieldWithPath("code").description("오류 코드")
                                                        , fieldWithPath("message").description("오류 코드 메세지")
                                                        , fieldWithPath("data").description("응답 데이터")
                                                )
                                                .build()
                                )
                        )
                );
    }

    @Test
    @DisplayName("인증서버 오류로 인한 통신 불가")
    public void loginFailCauseServer() throws Exception {
        // given
        String body = new ObjectMapper().writeValueAsString(LoginReqDto.builder().memberId("test12345").password("password123!@").build());

        doThrow(new CustomException(ErrorCode.LOGIN_FAIL_ERROR)).when(memberService).login(any(LoginReqDto.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.INTERNAL_SERVER_ERROR.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.LOGIN_FAIL_ERROR.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.LOGIN_FAIL_ERROR.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("auth-server-drop"
                                , preprocessRequest(prettyPrint())
                                , preprocessResponse(prettyPrint())
                                , resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("Member-Api")
                                                .requestSchema(schema("LoginReqDto"))
                                                .responseSchema(schema("ApiResponse"))
                                                .requestFields(
                                                        fieldWithPath("memberId").description("회원 아이디")
                                                        , fieldWithPath("password").description("비밀번호")
                                                )
                                                .responseFields(
                                                        fieldWithPath("status").description("응답 코드")
                                                        , fieldWithPath("error").description("응답 코드 명")
                                                        , fieldWithPath("code").description("오류 코드")
                                                        , fieldWithPath("message").description("오류 코드 메세지")
                                                        , fieldWithPath("data").description("응답 데이터")
                                                )
                                                .build()
                                )
                        )
                );
    }

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
                .login(any(LoginReqDto.class), any(HttpServletResponse.class));

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