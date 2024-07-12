package com.roze.api_gateway.filter;

import com.roze.api_gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RouteValidator routeValidator;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (routeValidator.isSecured.test(request)) {
                if (authMissing(request)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing authorization header");
                }

                final String token = request.getHeaders().getOrEmpty("Authorization").getFirst();

                if (token == null || !token.startsWith("Bearer ")) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid authorization header");
                }

                String authToken = token.substring(7);

                try {
                    jwtService.extractAllClaims(authToken);

                    List<String> requiredRoles = routeValidator.getRequiredRole(request);

                    if (!requiredRoles.isEmpty()) {
                        boolean hasRequiredRole = requiredRoles.stream()
                                .anyMatch(role -> jwtService.hasRole(authToken, role));
                        if (!hasRequiredRole) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Unauthorized access - insufficient role");
                        }
                    }

                    if (jwtService.isExpired(authToken)) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED, "Token is expired");
                    }

                } catch (Exception e) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized access");
                }
            }

            return chain.filter(exchange);
        };
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}