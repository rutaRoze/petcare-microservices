package com.roze.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoint = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/eureka"
    );

    public static final Map<String, List<String>> securedRoutes = Map.of(
            "/api/v1/users", List.of("VET"),
            "/api/v1/appointments", List.of("VET"),
            "/api/v1/users/{id}", List.of("OWNER", "VET"),
            "/api/v1/appointments/{id}", List.of("OWNER", "VET")

    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoint.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public List<String> getRequiredRole(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return securedRoutes.entrySet().stream()
                .filter(entry -> path.matches(entry.getKey().replace("{id}", "\\d+")))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(List.of());
    }
}
