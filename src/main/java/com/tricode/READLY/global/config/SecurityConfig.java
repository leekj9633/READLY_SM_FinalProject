package com.tricode.READLY.global.config;

import com.tricode.READLY.domain.member.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. BCryptPasswordEncoder를 스프링 빈으로 등록하여 Service에서 주입받아 사용할 수 있게 하기
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. HTTP 보안 설정 (회원가입, 로그인 API 접근 허용 및 세션 비활성화)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 CSRF(크로스 사이트 요청 위조) 방어 기능을 비활성화
                .csrf(csrf -> csrf.disable())
                // JWT를 사용하므로 스프링 시큐리티의 기본 세션 방식을 사용하지 않도록 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입과 로그인 API는 토큰 없이도 접근 가능해야 함
                        .requestMatchers("/api/members/signup", "/api/members/login").permitAll()
                        // AI 에이전트가 콜백으로 호출하는 엔드포인트 (AI 서버는 회원 JWT를 가지고 있지 않음)
                        .requestMatchers(HttpMethod.POST, "/api/book-clubs/*/chats").permitAll()
                        // 그 외의 모든 요청은 인증(토큰)이 필요하도록 설정
                        .anyRequest().authenticated()
                )
                // Authorization 헤더의 JWT를 검증해 SecurityContext를 채우는 필터를 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
