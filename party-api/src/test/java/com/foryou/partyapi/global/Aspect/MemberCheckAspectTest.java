package com.foryou.partyapi.global.Aspect;

import com.foryou.partyapi.global.constants.Constants;
import com.foryou.partyapi.global.error.CustomException;
import com.foryou.partyapi.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MemberCheckAspectTest {

    @InjectMocks
    private MemberCheckAspect aspect;

    @Test
    @DisplayName("게이트웨이를 통해 전달받은 ID와 PathVariable로 전달받은 ID가 다를경우 오류 발생")
    public void notMatchedMember() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constants.REQUEST_HEADER_MEMBER_ID, "NO ID");
        request.setRequestURI("/party/test123/owner");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            aspect.memberCheck();
        });

        // then
        assertEquals(ErrorCode.NOT_MATCHED_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }

    @Test
    @DisplayName("MEMBER-ID 헤더 정보가 null일경우 오류 발생")
    public void notExistMemberIdHeader() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/party/test123/owner");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        CustomException customException = assertThrows(CustomException.class, () -> {
            aspect.memberCheck();
        });

        // then
        assertEquals(ErrorCode.NOT_MATCHED_MEMBER_ID, customException.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getErrorCode().getHttpStatus());
    }
}