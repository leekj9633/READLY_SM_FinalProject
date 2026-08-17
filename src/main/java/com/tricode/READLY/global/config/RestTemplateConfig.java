package com.tricode.READLY.global.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    // 타임아웃을 주지 않으면 AI 서버가 응답하지 않을 때 요청 스레드가 무한정 붙잡힌다.
    // 연결 3초 / 응답 대기 10초를 넘기면 RestClientException으로 실패시킨다.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

}
