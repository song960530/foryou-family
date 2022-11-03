package com.foryou.partyapi.api.controller;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.partyapi.api.dto.request.PartyMemberReqDto;
import com.foryou.partyapi.api.dto.request.PartyOwnerReqDto;
import com.foryou.partyapi.api.dto.response.MyPartyResDto;
import com.foryou.partyapi.api.dto.response.PartyInfoResDto;
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
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.foryou.partyapi.testUtils.RestDocsUtils.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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

    @Test
    @DisplayName("내가 신청한 Ott 리스트 조회")
    public void searchMyOttList() throws Exception {
        // given
        List<MyPartyResDto> result = List.of(
                new MyPartyResDto(OttType.NETFLIX, PartyRole.MEMBER, "매칭중")
                , new MyPartyResDto(OttType.TVING, PartyRole.MEMBER, "매칭완료")
                , new MyPartyResDto(OttType.WAVVE, PartyRole.OWNER, "매칭중")
                , new MyPartyResDto(OttType.WATCHA, PartyRole.OWNER, "매칭완료")
        );

        doReturn(result).when(service).myParty(anyString());

        // when & then 482260
        mockMvc.perform(get("/myparty/{memberId}", "test12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.data[*].ott", notNullValue()))
                .andExpect(jsonPath("$.data[*].role", notNullValue()))
                .andExpect(jsonPath("$.data[*].status", anything()))
                .andDo(print())
                .andDo(document("my-party-list"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Party-Api"
                                        , "OTT 리스트 조회"
                                        , "내가 신청한 OTT 리스트 조회"
                                        , null
                                        , createDocPathVariable()
                                        , null
                                        , myPartyResponseFields()
                                )
                        )
                ));

    }

    @Test
    @DisplayName("파티 상세 조회")
    public void searchPartyDetail() throws Exception {
        // given
        PartyInfoResDto result = PartyInfoResDto.builder()
                .ott(OttType.NETFLIX)
                .inwon(4)
                .partyId("shard_id")
                .partyPassword("shard_password")
                .memberList(List.of(
                        PartyInfoResDto.PartyMember.builder().profile("파티원1프로파일").role(PartyRole.MEMBER).build()
                        , PartyInfoResDto.PartyMember.builder().profile("파티원2프로파일").role(PartyRole.MEMBER).build()
                        , PartyInfoResDto.PartyMember.builder().profile("파티장프로파일").role(PartyRole.OWNER).build()))
                .build();

        doReturn(result).when(service).partyInfo(anyLong());

        // when & then
        mockMvc.perform(get("/myparty/{memberId}/parties/{partyNo}", "test12345", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.data.ott", is(OttType.NETFLIX.getValue())))
                .andExpect(jsonPath("$.data.inwon", is(4)))
                .andExpect(jsonPath("$.data.partyId", is("shard_id")))
                .andExpect(jsonPath("$.data.partyPassword", is("shard_password")))
                .andExpect(jsonPath("$.data.memberList[*]", notNullValue()))
                .andDo(print())
                .andDo(document("party-detail"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Party-Api"
                                        , "파티 상세 조회"
                                        , "내가 속해있는 파티 중 하나를 상세조회한다"
                                        , null
                                        , createDocPathVariable2()
                                        , null
                                        , partyDetailResponseFields()
                                )
                        )
                ));
        ;
    }

    @Test
    @DisplayName("존재하지 않는 파티를 조회했을경우")
    public void notExistPartyDetail() throws Exception {
        // given
        doThrow(new CustomException(ErrorCode.NOT_EXIST_PARTY)).when(service).partyInfo(anyLong());

        // when
        mockMvc.perform(get("/myparty/{memberId}/parties/{partyNo}", "test12345", "999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.NOT_EXIST_PARTY.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.NOT_EXIST_PARTY.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("not-exist-party"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Party-Api"
                                        , null
                                        , createDocPathVariable2()
                                        , null
                                )
                        )
                ));
        // then
    }

    public static List<ParameterDescriptorWithType> createDocPathVariable2() {
        return List.of(parameterWithName("memberId").description("요청자 회원 ID")
                , parameterWithName("partyNo").description("파티 No"));
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

    private List<FieldDescriptor> myPartyResponseFields() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("data").description("응답 데이터")
                , fieldWithPath("data[].ott").description("OTT 종류")
                , fieldWithPath("data[].role").description("파티 역할 종류")
                , fieldWithPath("data[].status").description("매칭 진행 상태"));
    }

    private List<FieldDescriptor> partyDetailResponseFields() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("data").description("응답 데이터")
                , fieldWithPath("data.ott").description("OTT 종류")
                , fieldWithPath("data.inwon").description("파티 전체 인원")
                , fieldWithPath("data.partyId").description("공유 아이디")
                , fieldWithPath("data.partyPassword").description("공유 비밀번호")
                , fieldWithPath("data.memberList[].profile").description("동일 파티멤버 프로파일")
                , fieldWithPath("data.memberList[].role").description("동일 파티멤버 역할")
        );
    }
}















