package com.foryou.partyapi.global.Aspect;

import com.foryou.partyapi.global.constants.Constants;
import com.foryou.partyapi.global.error.CustomException;
import com.foryou.partyapi.global.error.ErrorCode;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/**
 * GATEWAY에서 설정한 MEMBER-ID와 PathVariable의 memberId 값 비교
 */
@Aspect
@Component
public class MemberCheckAspect {

    @Before("@annotation(com.foryou.partyapi.global.annotation.MemberCheck)")
    public void memberCheck() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String memberId = request.getHeader(Constants.REQUEST_HEADER_MEMBER_ID);

        long count = Stream.of(request.getRequestURI().split("/"))
                .filter(s -> s.equals(memberId))
                .count();

        if (count < 1)
            throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER_ID);
    }
}
