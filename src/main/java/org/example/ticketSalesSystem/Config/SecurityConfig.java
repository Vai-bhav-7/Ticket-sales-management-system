package org.example.ticketSalesSystem.config;

import org.example.ticketSalesSystem.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()

                        // Anyone authenticated can view events
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/events/**"
                        ).authenticated()

                        // Only ADMIN can modify events
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/events/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/events/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/events/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}