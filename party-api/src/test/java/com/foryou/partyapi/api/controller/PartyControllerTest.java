package com.foryou.partyapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.enums.OttType;
import com.foryou.partyapi.api.enums.PartyRole;
import com.foryou.partyapi.api.service.PartyService;
import com.foryou.partyapi.api.service.kafka.producer.KafkaProducer;
import com.foryou.partyapi.global.error.CustomException;
import com.foryou.partyapi.global.error.ErrorCode;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.foryou.partyapi.testUtils.RestDocsUtils.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(value = PartyController.class)
@MockBean(value = JpaMetamodelMappingContext.class)
class PartyControllerTest {

    private MockMvc mockMvc;
    @MockBean
    private PartyService service;
    @MockBean
    private KafkaProducer producer;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("파티원 등록 성공")
    public void successRegistMember() throws Exception {
        // given
        String content = mapper.writeValueAsString(
                PartyMemberReqDto.builder()
                        .ott(OttType.NETFLIX)
                        .role(PartyRole.MEMBER)
                        .paymentNo(1L)
                        .build()
        );

        // when & then
        mockMvc.perform(post("/party/{memberId}/member", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("party-member-create"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Party-Api"
                                        , "OTT 파티원 신청"
                                        , "OTT 파티의 파티원으로 신청하는 요청"
                                        , "PartyMemberReqDto"
                                        , createDocPathVariable()
                                        , memberRequestFields()
                                        , createSuccessDocResponseFields()
                                )
                        )
                ));

    }

    @Test
    @DisplayName("역할에 맞지 않는 URL로 신청을 헀을 경우")
    public void validPartyRole() throws Exception {
        // given
        String content = mapper.writeValueAsString(
                PartyMemberReqDto.builder()
                        .ott(OttType.NETFLIX)
                        .role(PartyRole.OWNER)
                        .paymentNo(1L)
                        .build()
        );

        doThrow(new CustomException(ErrorCode.ROLE_NOT_MATCHED)).when(service).createMemberParty(any(PartyMemberReqDto.class));

        // when & then
        mockMvc.perform(post("/party/{memberId}/member", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(HttpStatus.UNAUTHORIZED.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.UNAUTHORIZED.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.ROLE_NOT_MATCHED.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.ROLE_NOT_MATCHED.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("valid-member-role"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Party-Api"
                                        , "PartyMemberReqDto"
                                        , createDocPathVariable()
                                        , memberRequestFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("이미 존재하는 OTT 신청일 경우")
    public void existOtt() throws Exception {
        // given
        String content = mapper.writeValueAsString(
                PartyMemberReqDto.builder()
                        .ott(OttType.NETFLIX)
                        .role(PartyRole.MEMBER)
                        .paymentNo(1L)
                        .build()
        );

        doThrow(new CustomException(ErrorCode.DUPLICATE_OTT_JOIN)).when(service).createMemberParty(any(PartyMemberReqDto.class));

        // when & then
        mockMvc.perform(post("/party/{memberId}/member", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.CONFLICT.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.DUPLICATE_OTT_JOIN.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.DUPLICATE_OTT_JOIN.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("exist-ott-type"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Party-Api"
                                        , "PartyMemberReqDto"
                                        , createDocPathVariable()
                                        , memberRequestFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("파티장 등록 성공")
    public void successRegistOwner() throws Exception {
        // given
        String content = mapper.writeValueAsString(
                PartyOwnerReqDto.builder()
                        .role(PartyRole.OWNER)
                        .ott(OttType.NETFLIX)
                        .inwon(3)
                        .id("shard_id")
                        .password("shard_password")
                        .build()
        );

        // when & then
        mockMvc.perform(post("/party/{memberId}/owner", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("party-owner-create"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Party-Api"
                                        , "OTT 파티장 신청"
                                        , "OTT 파티의 파티장으로 신청하는 요청"
                                        , "PartyOwnerReqDto"
                                        , createDocPathVariable()
                                        , ownerRequestFields()
                                        , createSuccessDocResponseFields()
                                )
                        )
                ));

    }

    @Test
    @DisplayName("역할에 맞지 않는 URL로 신청을 헀을 경우_파티장")
    public void validPartyRoleOwner() throws Exception {
        String content = mapper.writeValueAsString(
                PartyOwnerReqDto.builder()
                        .role(PartyRole.MEMBER)
                        .ott(OttType.NETFLIX)
                        .inwon(3)
                        .id("shard_id")
                        .password("shard_password")
                        .build()
        );

        doThrow(new CustomException(ErrorCode.ROLE_NOT_MATCHED)).when(service).createOwnerParty(any(PartyOwnerReqDto.class));

        // when & then
        mockMvc.perform(post("/party/{memberId}/owner", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(HttpStatus.UNAUTHORIZED.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.UNAUTHORIZED.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.ROLE_NOT_MATCHED.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.ROLE_NOT_MATCHED.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("valid-owner-role"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Party-Api"
                                        , "PartyOwnerReqDto"
                                        , createDocPathVariable()
                                        , ownerRequestFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("이미 존재하는 OTT 신청일 경우_파티장")
    public void existOttOwner() throws Exception {
        // given
        String content = mapper.writeValueAsString(
                PartyOwnerReqDto.builder()
                        .role(PartyRole.OWNER)
                        .ott(OttType.NETFLIX)
                        .inwon(3)
                        .id("shard_id")
                        .password("shard_password")
                        .build()
        );

        doThrow(new CustomException(ErrorCode.DUPLICATE_OTT_JOIN)).when(service).createOwnerParty(any(PartyOwnerReqDto.class));

        // when & then
        mockMvc.perform(post("/party/{memberId}/owner", "test12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.CONFLICT.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.DUPLICATE_OTT_JOIN.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.DUPLICATE_OTT_JOIN.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("exist-ott-type"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Party-Api"
                                        , "PartyOwnerReqDto"
                                        , createDocPathVariable()
                                        , ownerRequestFields()
                                )
                        )
                ));
    }

    private List<FieldDescriptor> memberRequestFields() {
        return List.of(fieldWithPath("ott").description("신청 OTT 종류")
                , fieldWithPath("role").description("파티 멤버 신청 타입")
                , fieldWithPath("paymentNo").description("결제 카드 No"));
    }

    private List<FieldDescriptor> ownerRequestFields() {
        return List.of(fieldWithPath("role").description("파티 멤버 신청 타입")
                , fieldWithPath("ott").description("신청 OTT 종류")
                , fieldWithPath("inwon").description("모집 할 인원")
                , fieldWithPath("id").description("공유할 아이디")
                , fieldWithPath("password").description("공유할 비밀번호"));
    }
}















