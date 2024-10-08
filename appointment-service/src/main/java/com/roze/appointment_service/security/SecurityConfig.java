package com.roze.appointment_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private final AppointmentServiceJwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                            requests.requestMatchers("/actuator/**").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.GET, "/api/v1/appointments/{id}").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.PUT, "/api/v1/appointments/**").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.DELETE, "/api/v1/appointments/**").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.GET, "api/v1/appointments").permitAll();
                            requests.requestMatchers
                                    (HttpMethod.POST, "/api/v1/appointments").permitAll();

                            requests.anyRequest().authenticated();
                        }
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
