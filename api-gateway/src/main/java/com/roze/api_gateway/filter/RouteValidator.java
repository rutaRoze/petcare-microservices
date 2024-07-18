package com.roze.api_gateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openRoutes = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/eureka"
    );

    public static final Map<RouteKey, List<String>> securedRoutes = Map.of(
            RouteKey.builder()
                    .path(List.of("/api/v1/users", "/api/v1/appointments"))
                    .method(HttpMethod.GET)
                    .build(), List.of("VET"),
            RouteKey.builder()
                    .path(List.of("/api/v1/users/{id}", "/api/v1/appointments/{id}"))
                    .method(HttpMethod.GET)
                    .build(), List.of("OWNER", "VET"),
            RouteKey.builder()
                    .path(List.of("/api/v1/users", "/api/v1/appointments"))
                    .method(HttpMethod.POST)
                    .build(), List.of("OWNER", "VET"),
            RouteKey.builder()
                    .path(List.of("/api/v1/users/{id}", "/api/v1/appointments/{id}"))
                    .method(HttpMethod.PUT)
                    .build(), List.of("OWNER", "VET"),
            RouteKey.builder()
                    .path(List.of("/api/v1/users/{id}", "/api/v1/appointments/{id}"))
                    .method(HttpMethod.DELETE)
                    .build(), List.of("VET")
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openRoutes.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));


    public List<String> getRequiredRole(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        Optional<List<String>> roles = securedRoutes.entrySet().stream()
                .filter(entry -> {
                    RouteKey routeKey = entry.getKey();
                    return routeKey.getPath().stream()
                            .anyMatch(p -> path.matches(p.replace("{id}", "\\d+")) && method == routeKey.getMethod());
                })
                .map(Map.Entry::getValue)
                .findFirst();

        return roles.orElse(List.of());
    }
}
