package com.foryou.billingapi.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.foryou.billingapi.api.dto.request.CreatePaymentDto;
import com.foryou.billingapi.api.dto.response.CardListResDto;
import com.foryou.billingapi.api.service.PaymentService;
import com.foryou.billingapi.global.error.CustomException;
import com.foryou.billingapi.global.error.ErrorCode;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.foryou.billingapi.testUtils.RestDocsUtils.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
@WebMvcTest(value = PaymentController.class)
@MockBean(value = JpaMetamodelMappingContext.class)
class PaymentControllerTest {

    private MockMvc mockMvc;
    @MockBean
    private PaymentService service;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        this.mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("결제카드 정상생성")
    public void successPayment() throws Exception {
        // given
        String body = mapper.writeValueAsString(CreatePaymentDto.builder()
                .cardNum("hqhZbLWfDL9r6kBdPSRmU8LpzjTFY/vWc3hh5O3/rDE=")
                .expiredDate("gk0d3m+bx3NN9lULXxnZiQ==")
                .birthDate("6E4EVjS2zs4uvnArEdd0zg==")
                .pwd2digit("5ALcr/W47lBuO9WOQmu+Kw==")
                .build());

        // when & then
        mockMvc.perform(
                        post("/payments/{memberId}", "test12345")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("payment-create"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Billing-Api"
                                        , "결제 카드 생성"
                                        , "결제 카드를 생성한다<br>결제카드는 정상카드 확인을 위하여 최초 100원이 결제되고 확인 후 바로 취소처리된다<br>요청 정보는 AES256으로 암호화하여 전달한다"
                                        , "CreatePaymentDto"
                                        , createDocPathVariable()
                                        , requestPaymentDtoFields()
                                        , createSuccessDocResponseFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("복호화 요청이 잘못들어왔을 때")
    public void decryptFail() throws Exception {
        // given
        String body = mapper.writeValueAsString(CreatePaymentDto.builder()
                .cardNum("Unencrypted Sentences")
                .expiredDate("Unencrypted Sentences")
                .birthDate("Unencrypted Sentences")
                .pwd2digit("Unencrypted Sentences")
                .build());

        doThrow(new CustomException(ErrorCode.CIPHER_DECRYPT_ERROR)).when(service).createOnetimePaymentData(anyString(), any(CreatePaymentDto.class), anyString(), any(BigDecimal.class));

        // when & then
        mockMvc.perform(
                        post("/payments/{memberId}", "test12345")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.CIPHER_DECRYPT_ERROR.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.CIPHER_DECRYPT_ERROR.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("fail-decrypt"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Billing-Api"
                                        , "CreatePaymentDto"
                                        , createDocPathVariable()
                                        , requestPaymentDtoFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("카드 등록을 실패했을 때")
    public void careRegFail() throws Exception {
        // given
        String body = mapper.writeValueAsString(CreatePaymentDto.builder()
                .cardNum("hqhZbLWfDL9r6kBdPSRmU8LpzjTFY/vWc3hh5O3/rDE=")
                .expiredDate("gk0d3m+bx3NN9lULXxnZiQ==")
                .birthDate("6E4EVjS2zs4uvnArEdd0zg==")
                .pwd2digit("5ALcr/W47lBuO9WOQmu+Kw==")
                .build());

        doThrow(new CustomException(ErrorCode.CARD_REGISTRATION_FAILED)).when(service).doFirstPay(any());

        // when & then
        mockMvc.perform(
                        post("/payments/{memberId}", "test12345")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.error", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.code", is(ErrorCode.CARD_REGISTRATION_FAILED.name())))
                .andExpect(jsonPath("$.message", is(ErrorCode.CARD_REGISTRATION_FAILED.getMessage())))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andDo(print())
                .andDo(document("fail-card-regist"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createFailDoc(
                                        "Billing-Api"
                                        , "CreatePaymentDto"
                                        , createDocPathVariable()
                                        , requestPaymentDtoFields()
                                )
                        )
                ));
    }

    @Test
    @DisplayName("카드 리스트 조회")
    public void searchCardList() throws Exception {
        // given
        CardListResDto result = CardListResDto.builder()
                .memberId("test12345")
                .count(2)
                .paymentCardList(List.of(CardListResDto.CardList.builder()
                        .paymentNo(1L)
                        .cardNum4Digit("9999")
                        .createDate(LocalDateTime.now())
                        .build(), CardListResDto.CardList.builder()
                        .paymentNo(2L)
                        .cardNum4Digit("8786")
                        .createDate(LocalDateTime.now())
                        .build()))
                .build();

        doReturn(result).when(service).myPaymentCardList(anyString());

        // when & then
        mockMvc.perform(
                        get("/payments/{memberId}", "test12345")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.data.count", is(2)))
                .andExpect(jsonPath("$.data.memberId", is("test12345")))
                .andExpect(jsonPath("$.data.paymentCardList[0].paymentNo", is(1)))
                .andExpect(jsonPath("$.data.paymentCardList[0].cardNum4Digit", is("9999")))
                .andExpect(jsonPath("$.data.paymentCardList[0].createDate", notNullValue()))
                .andExpect(jsonPath("$.data.paymentCardList[1].paymentNo", is(2)))
                .andExpect(jsonPath("$.data.paymentCardList[1].cardNum4Digit", is("8786")))
                .andExpect(jsonPath("$.data.paymentCardList[1].createDate", notNullValue()))
                .andDo(print())
                .andDo(document("search-cardList"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , resource(
                                createSuccessDoc(
                                        "Billing-Api"
                                        , "결제 카드 조회"
                                        , "사용 가능한 결제 카드 리스트 조회"
                                        , null
                                        , createDocPathVariable()
                                        , null
                                        , responseFieldsCardList()
                                )
                        )
                ));
    }

    private List<FieldDescriptor> responseFieldsCardList() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("data").description("응답 데이터")
                , fieldWithPath("data.count").description("카드 개수")
                , fieldWithPath("data.memberId").description("회원 명")
                , fieldWithPath("data.paymentCardList").description("카드 리스트")
                , fieldWithPath("data.paymentCardList[].paymentNo").description("결제 카드 No")
                , fieldWithPath("data.paymentCardList[].cardNum4Digit").description("카드 뒤 4자리")
                , fieldWithPath("data.paymentCardList[].createDate").description("결제 카드 생성일")
        );
    }

    private List<FieldDescriptor> requestPaymentDtoFields() {
        return List.of(fieldWithPath("cardNum").description("카드번호 '-' 포함")
                , fieldWithPath("expiredDate").description("만료일 '-' 포함")
                , fieldWithPath("birthDate").description("생년월일 6자리")
                , fieldWithPath("pwd2digit").description("카드 비밀번호 앞 2자리"));
    }
}