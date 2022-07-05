package com.foryou.gatewayservice.filter;

import com.foryou.gatewayservice.constants.Constants;
import com.foryou.gatewayservice.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

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
            log.info("{} START >>>>>>", config.getBaseMessage());
            log.info("Request URI: {}", exchange.getRequest().getURI());
            log.info("Request Authorization: {}", exchange.getRequest().getHeaders().get("Authorization"));

            extractClaims(exchange)
                    .ifPresent(claims -> addHeader(exchange, claims));

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("{} END >>>>>>", config.getBaseMessage());
            }));
        });
    }

    private Optional<Claims> extractClaims(ServerWebExchange exchange) {
        return Optional.of(jwtTokenProvider.extractToken(exchange))
                .filter(token -> !token.equals(Constants.DEFAULT_TOKEN_VALUE))
                .map(token -> jwtTokenProvider.extractClaims(token));
    }

    private void addHeader(ServerWebExchange exchange, Claims claims) {
        exchange.getRequest().mutate()
                .header("ROLES", String.join(" ", (List<String>) claims.get("roles")))
                .header("MEMBER-ID", claims.getSubject())
                .build();
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
    }
}
