package com.foryou.gatewayservice.filter;

import com.foryou.gatewayservice.constants.Constants;
import com.foryou.gatewayservice.exception.CustomException;
import com.foryou.gatewayservice.exception.ErrorCode;
import com.foryou.gatewayservice.util.GateWayUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MemberFilter extends AbstractGatewayFilterFactory<MemberFilter.Config> {

    public MemberFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            HttpMethod requestMethod = exchange.getRequest().getMethod();
            String requestPath = exchange.getRequest().getPath().toString();

            if (!GateWayUtils.isExcludePath(config.getExcludePathList(), requestMethod, requestPath)) {
                String memberId = exchange.getRequest().getHeaders().get(Constants.HEADER_MEMBER_NAME).get(0);

                if (Constants.DEFAULT_TOKEN_VALUE.equals(memberId))
                    throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);

                if (!GateWayUtils.isExistMemberIdInPath(memberId, requestPath))
                    throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER_ID_TOKEN);
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            }));
        });
    }

    @Getter
    public static class Config {
        @Setter
        private String baseMessage;
        private String excludePath;
        private List<Map<String, String>> excludePathList;

        public void setExcludePath(String excludePath) {
            this.excludePath = excludePath;
            excludePathList = GateWayUtils.changePathToListMap(excludePath);
        }
    }
}
