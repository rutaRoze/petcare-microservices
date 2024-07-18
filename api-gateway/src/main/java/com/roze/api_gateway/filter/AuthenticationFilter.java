package com.roze.api_gateway.filter;

import com.roze.api_gateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JwtService jwtService;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
                }

                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).getFirst();

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
                }

                final String finalAuthHeader = authHeader;

                try {
                    jwtService.extractAllClaims(finalAuthHeader);
                    List<String> requiredRoles = routeValidator.getRequiredRole(exchange.getRequest());

                    if (!requiredRoles.isEmpty()) {
                        boolean hasRequiredRole = requiredRoles.stream()
                                .anyMatch(role -> jwtService.hasRole(finalAuthHeader, role));
                        if (!hasRequiredRole) {
                            throw new RuntimeException("Unauthorized access - insufficient role");
                        }
                    }
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {

    }
}
