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

            Optional<Claims> claims = extractClaims(exchange);

            if (claims.isPresent()) {
                addHeader(exchange, claims);
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("{} END >>>>>>", config.getBaseMessage());
                removeHeader(exchange);
            }));
        });
    }

    private Optional<Claims> extractClaims(ServerWebExchange exchange) {
        return Optional.of(jwtTokenProvider.extractToken(exchange))
                .filter(token -> !token.equals(Constants.DEFAULT_TOKEN_VALUE))
                .map(token -> jwtTokenProvider.extractRoles(token));
    }

    private void removeHeader(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().remove("ROLES");
        exchange.getResponse().getHeaders().remove("MEMBERID");
        exchange.getResponse().getHeaders().remove("PATH");
    }

    private void addHeader(ServerWebExchange exchange, Optional<Claims> claims) {
        exchange.getResponse().getHeaders().set("ROLES", String.join(" ", (List<String>) claims.get().get("roles")));
        exchange.getResponse().getHeaders().set("MEMBERID", claims.get().getSubject());
        exchange.getResponse().getHeaders().set("PATH", exchange.getRequest().getPath().toString());
    }

    @Getter
    @Setter
    public static class Config {
        private String baseMessage;
    }
}
