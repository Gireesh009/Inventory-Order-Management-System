package org.ibs.productservice.config;

import lombok.RequiredArgsConstructor;
import org.ibs.productservice.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthentication authEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        )
                .authorizeHttpRequests(auth -> auth

                        // Swagger (public)
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // PRODUCT SERVICE (catalog only)

                        // public read access
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        // admin only product management
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")

                        // INVENTORY SERVICE (separate responsibility)

                        // view stock
                        .requestMatchers(HttpMethod.GET, "/inventory/**")
                        .hasAnyRole("ADMIN", "USER")

                        // modify stock (admin only)
                        .requestMatchers(HttpMethod.PUT, "/inventory/update")
                        .hasRole("ADMIN")
                        // stock reduction (used by order flow)
                        .requestMatchers(HttpMethod.PUT, "/inventory/reduce")
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(HttpMethod.GET, "/inventory/low-stock/")
                        .hasAnyRole("ADMIN")
                        // fallback
                        .anyRequest().authenticated() // all product endpoints secured
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}