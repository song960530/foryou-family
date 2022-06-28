package com.foryou.gatewayservice.filter;

import com.foryou.gatewayservice.constants.Constants;
import com.foryou.gatewayservice.jwt.JwtTokenProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtGlobalFilter extends AbstractGatewayFilterFactory<JwtGlobalFilter.Config> {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtGlobalFilter(JwtTokenProvider jwtTokenProvider) {
        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info(config.getBaseMessage() + " START >>>>>>");

            List<String> roles = Stream.of(jwtTokenProvider.extractToken(exchange))
                    .filter(t -> !t.equals(Constants.DEFAULT_TOKEN_VALUE))
                    .map(t -> jwtTokenProvider.extractRoles(t))
                    .findAny()
                    .orElse(new ArrayList<>());

            exchange.getResponse().getHeaders().set("roles", String.join(" ", roles));

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info(config.getBaseMessage() + " END >>>>>>");
            }));
        });
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
    }
}
