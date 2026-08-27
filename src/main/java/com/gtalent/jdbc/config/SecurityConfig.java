package com.gtalent.jdbc.config;

import com.gtalent.jdbc.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 關閉 CSRF
                .csrf(csrf -> csrf.disable())

                // JWT 不使用 Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 401 / 403 錯誤處理
                .exceptionHandling(ex -> ex

                        // 沒登入或 Token 無效 → 401
                        .authenticationEntryPoint(
                                (request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED
                                    );
                                }
                        )

                        // 已登入，但權限不足 → 403
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {
                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );
                                }
                        )
                )

                // API 權限設定
                .authorizeHttpRequests(auth -> auth


                        // 前端靜態檔案不用登入
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/style.css",
                                "/app.js",
                                "/favicon.ico")
                        .permitAll()

                        // 註冊、登入不需要 JWT
                        .requestMatchers("/auth/**")
                        .permitAll()

                        // Swagger 不需要 JWT
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // 查詢會員：USER、ADMIN 都可以
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/members/**"
                        )
                        .hasAnyRole("USER", "ADMIN")

                        // 新增會員：只有 ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/members/**"
                        )
                        .hasRole("ADMIN")

                        // 修改會員：只有 ADMIN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/members/**"
                        )
                        .hasRole("ADMIN")

                        // 刪除會員：只有 ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/members/**"
                        )
                        .hasRole("ADMIN")

                        // 其他 API 至少要登入
                        .anyRequest()
                        .authenticated()
                )

                // JWT Filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}