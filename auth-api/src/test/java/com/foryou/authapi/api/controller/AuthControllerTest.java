package com.foryou.authapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.authapi.api.dto.TokenResDto;
import com.foryou.authapi.api.service.AuthService;
import com.foryou.authapi.global.constants.Constants;
import com.foryou.authapi.global.error.CustomException;
import com.foryou.authapi.global.error.ErrorCode;
import com.foryou.authapi.global.jwt.JwtTokenProvider;
import com.foryou.authapi.global.properties.JwtProperties;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.foryou.authapi.testUtils.RestDocsUtils.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(value = AuthController.class)
@MockBean(value = JpaMetamodelMappingContext.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    @MockBean
    private AuthService service;
    private JwtTokenProvider provider;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        initTokenProvider();
        this.mapper = new ObjectMapper();
    }

    private void initTokenProvider() {
        JwtProperties properties = new JwtProperties();
        ReflectionTestUtils.setField(properties, "secretKey", "01234567890123456789012345678912");
        ReflectionTestUtils.setField(properties, "accessValidTime", 30);
        ReflectionTestUtils.setField(properties, "refreshValidTime", 1);
        provider = new JwtTokenProvider(properties);
        provider.init();
    }

    @Test
    @DisplayName("JWT 재발급 성공")
    public void successReAuth() throws Exception {
        // given
        String memberId = "test12345";
        String accessToken = provider.createAccessToken(memberId);
        TokenResDto result = TokenResDto.builder()
                .accessToken(accessToken)
                .refreshToken("httponly")
                .type(Constants.TOKEN_TYPE)
                .build();

        doReturn(result).when(service).reCreateToken(anyString(), any(), any());

        // when & then
        mockMvc.perform(patch("/reAuth/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.data.accessToken", is(accessToken)))
                .andExpect(jsonPath("$.data.refreshToken", is("httponly")))
                .andExpect(jsonPath("$.data.type", is(Constants.TOKEN_TYPE)))
                .andDo(print())
                .andDo(document("party-member-create"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDocWithHeader(
                                        "Auth-Api"
                                        , "JWT 토큰 재발급"
                                        , "기존 발급되었던 토큰 정보를 확인하여 새로운 토큰을 발급합니다<br>리프레쉬 토큰은 cookie에 httponly로 발급됩니다<br>요청 시 Cookie에 Refresh-Token 정보가 저장되어 있어야 합니다"
                                        , null
                                        , createDocPathVariable()
                                        , null
                                        , reAuthResponseFields()
                                        , null
                                )
                        )
                ));
    }

    @Test
    @DisplayName("Header에 쿠기가 없을 경우")
    public void notExistCookie() throws Exception {
        // given
        String memberId = "test12345";

        doThrow(new CustomException(ErrorCode.NOt_EXIST_REFRESH_TOKEN)).when(service).reCreateToken(anyString(), any(), any());

        // when & then
        mockMvc.perform(patch("/reAuth/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.NOt_EXIST_REFRESH_TOKEN.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.NOt_EXIST_REFRESH_TOKEN.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("not-exist-cookie"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Auth-Api"
                                        , null
                                        , createDocPathVariable()
                                        , null
                                )
                        )
                ));
    }

    @Test
    @DisplayName("발급 이력이 없는 토큰")
    public void notExistToken() throws Exception {
        // given
        String memberId = "test12345";

        doThrow(new CustomException(ErrorCode.ARGUMENT_NOT_VALID)).when(service).reCreateToken(anyString(), any(), any());

        // when & then
        mockMvc.perform(patch("/reAuth/{memberId}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.ARGUMENT_NOT_VALID.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.ARGUMENT_NOT_VALID.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("not-exist-token"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Auth-Api"
                                        , null
                                        , createDocPathVariable()
                                        , null
                                )
                        )
                ));
    }

    private List<FieldDescriptor> reAuthResponseFields() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("data").description("응답 데이터")
                , fieldWithPath("data.accessToken").description("액세스 토큰")
                , fieldWithPath("data.refreshToken").description("리프레쉬 토큰(cookie에 발급)")
                , fieldWithPath("data.type").description("인증 타입")

        );
    }
}