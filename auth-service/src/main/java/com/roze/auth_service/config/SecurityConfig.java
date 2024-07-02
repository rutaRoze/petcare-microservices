package com.roze.auth_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                            requests.requestMatchers
                                    (HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/appointments").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.GET, "/api/v1/users/{id}", "/api/v1/appointments/**").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.PUT, "/api/v1/users/**", "/api/v1/appointments/**").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.DELETE, "/api/v1/users/**", "/api/v1/appointments/**").hasAnyAuthority("VET");
                            requests.requestMatchers
                                    (HttpMethod.GET, "api/v1/users").hasAnyAuthority("VET");
                            requests.requestMatchers
                                    (HttpMethod.POST, "/api/v1/users").hasAnyAuthority("VET");

                            requests.anyRequest().authenticated();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
