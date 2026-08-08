package com.tricode.READLY.global.config;

import com.tricode.READLY.domain.member.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

// WebSocket(STOMP)은 HTTP 필터를 타지 않으므로, 연결(CONNECT) 시점에 JWT를 따로 검증한다.
// 검증에 성공하면 memberId를 세션에 심어두고, 이후 채팅 메시지는 이 값을 사용한다.
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 연결을 맺을 때 한 번만 검사하면, 이후 메시지는 이때 저장한 사용자 정보를 따라간다
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다. 채팅에 연결할 수 없습니다.");
            }

            Long memberId = jwtTokenProvider.getMemberId(token);
            accessor.setUser(new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList()));
        }

        return message;
    }

    private String resolveToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
