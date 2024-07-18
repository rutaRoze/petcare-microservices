package com.roze.appointment_service.security;

import com.roze.appointment_service.feign.AuthServiceClient;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserServiceJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceJwtAuthenticationFilter.class);

    private final AuthServiceClient authServiceClient;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader("Authorization");
        final String jwt;

        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authenticationHeader.substring(7);

        try {
            Boolean isValid = validateTokenOrThrow("Bearer " + jwt);
            if (!isValid) {
                logger.debug("JWT token is not valid or expired");
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
                return;
            }
        } catch (FeignException e) {
            if (e.status() == 403) {
                logger.error("Forbidden: {}", e.getMessage());
                setErrorResponse(response, HttpStatus.FORBIDDEN, "Access Denied");
                return;
            } else if (e.status() == 401) {
                logger.error("Unauthorized: {}", e.getMessage());
                setErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized");
                return;
            } else {
                logger.error("Failed to process authentication", e);
                setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
                return;
            }
        } catch (Exception e) {
            logger.error("Failed to process authentication", e);
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Boolean validateTokenOrThrow(String token) {
        return authServiceClient.validateToken(token);
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}