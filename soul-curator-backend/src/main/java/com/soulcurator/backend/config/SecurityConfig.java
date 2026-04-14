package com.soulcurator.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // 允许所有请求
            )
            .csrf(csrf -> csrf.disable())  // 禁用CSRF（开发环境）
            .cors(cors -> cors.disable()); // 禁用CORS（我们已经在WebConfig中配置了）
        
        return http.build();
    }
}