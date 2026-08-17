package com.tricode.READLY.global.config;

import com.tricode.READLY.domain.book.repository.MemberBookClubRepository;
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

import java.security.Principal;
import java.util.Collections;

// WebSocket(STOMP)은 HTTP 필터를 타지 않으므로, 연결(CONNECT) 시점에 JWT를 따로 검증한다.
// 검증에 성공하면 memberId를 세션에 심어두고, 이후 채팅 메시지는 이 값을 사용한다.
// 구독(SUBSCRIBE)은 여기서 한 번 더 가입 여부를 확인한다. 발행만 막으면
// 가입하지 않은 회원이 clubId만 알아내 남의 모임 대화를 그대로 수신할 수 있기 때문이다.
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CHAT_SUB_PREFIX = "/sub/chat/clubs/";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberBookClubRepository memberBookClubRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        // 연결을 맺을 때 한 번만 검사하면, 이후 메시지는 이때 저장한 사용자 정보를 따라간다
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));

            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다. 채팅에 연결할 수 없습니다.");
            }

            Long memberId = jwtTokenProvider.getMemberId(token);
            accessor.setUser(new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList()));
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validateSubscription(accessor);
        }

        return message;
    }

    // 채팅방 구독은 가입한 회원만 허용한다. 채팅 외 목적지는 검사 없이 통과시킨다.
    private void validateSubscription(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(CHAT_SUB_PREFIX)) {
            return;
        }

        Long clubId = parseClubId(destination);
        if (clubId == null) {
            throw new MessageDeliveryException("잘못된 채팅방 주소입니다: " + destination);
        }

        Long memberId = resolveMemberId(accessor.getUser());
        if (memberId == null) {
            throw new MessageDeliveryException("인증되지 않은 연결입니다. 채팅방을 구독할 수 없습니다.");
        }

        // 발행(ChatService.validateClubMember)과 동일한 기준으로 확인한다
        if (!memberBookClubRepository.existsByMemberIdAndBookClubId(memberId, clubId)) {
            throw new MessageDeliveryException("가입하지 않은 독서모임의 채팅방은 구독할 수 없습니다.");
        }
    }

    // "/sub/chat/clubs/{clubId}" 뒤쪽만 잘라낸다. 하위 경로가 더 붙거나 숫자가 아니면 거부한다.
    private Long parseClubId(String destination) {
        String suffix = destination.substring(CHAT_SUB_PREFIX.length());
        try {
            return Long.parseLong(suffix);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // CONNECT에서 심어둔 인증 객체의 principal이 memberId(Long)다
    private Long resolveMemberId(Principal user) {
        if (user instanceof UsernamePasswordAuthenticationToken authentication
                && authentication.getPrincipal() instanceof Long memberId) {
            return memberId;
        }
        return null;
    }

    private String resolveToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return authorizationHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
