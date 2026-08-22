package com.tricode.READLY.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    // 소켓 연결을 허용할 출처. REST와 같은 목록을 쓴다 (SecurityConfig의 CORS 설정과 동일한 값)
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    // 클라이언트가 보내는 모든 STOMP 메시지가 거쳐가는 채널에 인증 인터셉터를 건다
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 웹소켓 연결을 맺는 엔드포인트.
        // 같은 경로를 두 번 등록한다.
        // 1) 순수 WebSocket: 정확히 /ws/chat 으로 들어온다. 프론트의 @stomp/stompjs가 이 방식으로 붙는다.
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns(allowedOrigins);

        // 2) SockJS: 폴백용. /ws/chat/info, /ws/chat/{server}/{session}/websocket 같은 하위 경로를 쓰므로
        //    1)의 정확 매칭과 충돌하지 않는다. (SecurityConfig가 /ws/chat/** 를 permitAll 해 둔 이유이기도 하다)
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독(수신)하는 요청 엔드포인트
        registry.enableSimpleBroker("/sub");
        // 메시지를 발행(송신)하는 엔드포인트
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
