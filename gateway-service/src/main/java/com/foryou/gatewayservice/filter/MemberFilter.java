package com.foryou.gatewayservice.filter;

import com.foryou.gatewayservice.exception.CustomException;
import com.foryou.gatewayservice.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class MemberFilter extends AbstractGatewayFilterFactory<MemberFilter.Config> {

    public MemberFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String roles = exchange.getResponse().getHeaders().get("roles").get(0);
            String requestPath = exchange.getResponse().getHeaders().get("path").get(0);

            if (!config.getExcludePath().contains(requestPath)) {
                if (roles.indexOf(config.getRequiredRole()) < 0) {
                    throw new CustomException(ErrorCode.PERMISSION_DENIED);
                }
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
        private String requiredRole;

        public List<String> getExcludePath() {
            return List.of(excludePath.split(" "));
        }
    }
}
