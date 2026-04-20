package com.soulcurator.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173", // Vite开发服务器
        "http://localhost:3000", // 其他可能的开发端口
        "http://127.0.0.1:5173",
        "http://127.0.0.1:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 配置CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // 明确允许分析接口匿名访问
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(
                "/api/v1/analytics/**", // 所有分析接口
                "/api/v1/hello", // 测试接口
                "/error", // 错误页面
                "/favicon.ico" // 网站图标
            ).permitAll()
            // 其他所有请求也允许匿名访问（项目初期不需要认证）
            .anyRequest().permitAll())
        // 禁用表单登录和基本认证
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        // 禁用登录/注销页面
        .logout(logout -> logout.disable())
        // 禁用会话管理（允许匿名访问）
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 完全禁用CSRF（API项目不需要）
        .csrf(csrf -> csrf.disable())
        // 禁用默认的认证入口点，避免重定向到登录页面
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
              // 直接返回401错误，而不是重定向到登录页面
              response.setStatus(401);
              response.setContentType("application/json");
              response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
            }));

    return http.build();
  }
}