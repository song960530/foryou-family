package com.foryou.memberapi.testUtils;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class RestDocsUtils {
    public static ResourceSnippetParameters createSuccessDoc(
            String tag
            , String summary
            , String description
            , String requestSchema
            , List<ParameterDescriptorWithType> pathParameters
            , List<FieldDescriptor> requestFieldsDescription
            , List<FieldDescriptor> responseFieldsDescription
    ) {
        return ResourceSnippetParameters.builder()
                .tag(tag)
                .summary(summary)
                .description(description)
                .requestSchema((requestSchema == null) ? null : schema(requestSchema))
                .responseSchema(schema("ApiResponse"))
                .pathParameters((pathParameters == null) ? Collections.emptyList() : pathParameters)
                .requestFields((requestFieldsDescription == null) ? Collections.emptyList() : requestFieldsDescription)
                .responseFields((responseFieldsDescription == null) ? Collections.emptyList() : responseFieldsDescription)
                .build();
    }

    public static ResourceSnippetParameters createFailDoc(
            String tag
            , String requestSchema
            , List<ParameterDescriptorWithType> pathParameters
            , List<FieldDescriptor> requestFieldsDescription
    ) {
        return ResourceSnippetParameters.builder()
                .tag(tag)
                .requestSchema((requestSchema == null) ? null : schema(requestSchema))
                .responseSchema(schema("ApiErrorResponse"))
                .pathParameters((pathParameters == null) ? Collections.emptyList() : pathParameters)
                .requestFields((requestFieldsDescription == null) ? Collections.emptyList() : requestFieldsDescription)
                .responseFields(createFailDocResponseFields())
                .build();
    }

    public static List<FieldDescriptor> createFailDocResponseFields() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("error").description("응답 코드 명")
                , fieldWithPath("code").description("오류 코드")
                , fieldWithPath("message").description("오류 코드 메세지")
                , fieldWithPath("data").description("응답 데이터"));
    }

    public static List<FieldDescriptor> createSuccessDocResponseFields() {
        return List.of(fieldWithPath("status").description("응답 코드")
                , fieldWithPath("data").description("응답 데이터"));
    }

    public static List<ParameterDescriptorWithType> createDocPathVariable() {
        return List.of(parameterWithName("memberId").description("요청자 회원 ID"));
    }
}
