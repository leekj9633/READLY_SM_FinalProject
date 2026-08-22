package com.tricode.READLY.global.config;

import com.tricode.READLY.domain.member.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 브라우저에서 이 API를 호출할 수 있는 출처 목록. 환경마다 다르므로 환경변수로 주입한다.
    // 콤마로 구분해 여러 개를 넣을 수 있다 (예: "http://localhost:3000,https://readly.vercel.app")
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    // 1. BCryptPasswordEncoder를 스프링 빈으로 등록하여 Service에서 주입받아 사용할 수 있게 하기
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. HTTP 보안 설정 (회원가입, 로그인 API 접근 허용 및 세션 비활성화)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 프론트엔드가 다른 출처(포트)에서 돌아가므로 CORS를 켠다.
                // 이 설정을 켜면 스프링 시큐리티가 CorsFilter를 필터 체인 맨 앞에 넣어
                // preflight(OPTIONS)를 거기서 바로 응답하고 끝낸다.
                // 즉 preflight는 아래 인가 규칙이나 JwtAuthenticationFilter까지 내려오지 않으므로
                // OPTIONS를 따로 permitAll 할 필요가 없다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                        // 웹소켓 핸드셰이크는 인증에서 제외한다.
                        // 브라우저의 WebSocket/SockJS는 핸드셰이크 요청에 Authorization 헤더를 넣을 수 없다.
                        // 대신 StompAuthChannelInterceptor가 CONNECT 프레임에서 JWT를 검증한다.
                        // SockJS는 /ws/chat/info, /ws/chat/{server}/{session}/websocket 등 하위 경로를 쓰므로 /** 가 필요하다.
                        .requestMatchers("/ws/chat/**").permitAll()
                        // 그 외의 모든 요청은 인증(토큰)이 필요하도록 설정
                        .anyRequest().authenticated()
                )
                // Authorization 헤더의 JWT를 검증해 SecurityContext를 채우는 필터를 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // setAllowedOrigins가 아니라 패턴으로 두면 "https://*.vercel.app" 같은 미리보기 도메인도 받을 수 있다
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        // Authorization, Content-Type, AI 서버가 쓰는 X-AI-API-KEY를 모두 허용한다
        configuration.setAllowedHeaders(List.of("*"));
        // preflight 결과를 1시간 캐시해 요청 수를 줄인다
        configuration.setMaxAge(3600L);
        // 토큰을 쿠키가 아니라 Authorization 헤더로 보내므로 자격증명 허용은 켜지 않는다
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
