package com.gabriel.springboot.app.menuflow.security;

import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.SecurityConstants.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SpringSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, REGISTER_URL).hasAnyRole(ROLE_ADMIN)
                        .requestMatchers(API_AUTH).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, ANY_PATH).permitAll()
                        .requestMatchers(SWAGGER_UI_PATH, API_DOCS_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, DISHES_ANY_PATH).permitAll()
                        .requestMatchers(HttpMethod.GET, CATEGORIES_ANY_PATH).permitAll()
                        .requestMatchers(DINING_TABLES_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CASHIER)
                        .requestMatchers(SESSION_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CASHIER)
                        .requestMatchers(HttpMethod.POST, DISHES_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CHEF)
                        .requestMatchers(HttpMethod.POST, CATEGORIES_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CHEF)
                        .requestMatchers(HttpMethod.PUT, DISHES_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CHEF)
                        .requestMatchers(HttpMethod.DELETE, DISHES_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CHEF)
                        .requestMatchers(KITCHEN_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_KITCHEN)
                        .requestMatchers(INVOICE_ANY_PATH).hasAnyRole(ROLE_ADMIN, ROLE_CASHIER)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(APPLICATION_JSON);
                            String json = new ObjectMapper().writeValueAsString(ApiResponse.error(authException.getMessage()));
                            response.getWriter().write(json);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(APPLICATION_JSON);
                            String json = new ObjectMapper().writeValueAsString(ApiResponse.error(ACCESS_DENIED_MESSAGE));
                            response.getWriter().write(json);
                        })
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(ALL_RESOURCES));
        config.setAllowedMethods(List.of(HTTP_GET,HTTP_POST,HTTP_PUT,HTTP_DELETE,HTTP_OPTIONS));
        config.setAllowedHeaders(List.of(ALL_RESOURCES));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(ANY_PATH, config);

        return source;
    }
}
