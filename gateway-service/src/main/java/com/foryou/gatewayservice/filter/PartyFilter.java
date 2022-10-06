package com.foryou.gatewayservice.filter;

import com.foryou.gatewayservice.constants.Constants;
import com.foryou.gatewayservice.exception.CustomException;
import com.foryou.gatewayservice.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class PartyFilter extends AbstractGatewayFilterFactory<PartyFilter.Config> {

    public PartyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String requestPath = exchange.getRequest().getPath().toString();

            if (!config.getExcludePath().contains(requestPath)) {
                String memberId = exchange.getRequest().getHeaders().get(Constants.HEADER_MEMBER_NAME).get(0);

                if (Constants.DEFAULT_TOKEN_VALUE.equals(memberId))
                    throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);

                boolean isMatch = Arrays.stream(requestPath.split("/"))
                        .anyMatch(splitPath -> splitPath.equals(memberId));

                if (!isMatch)
                    throw new CustomException(ErrorCode.NOT_MATCHED_MEMBER_ID_TOKEN);
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            }));
        });
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
        private String excludePath;

        public List<String> getExcludePath() {
            return List.of(excludePath.split(" "));
        }
    }
}
