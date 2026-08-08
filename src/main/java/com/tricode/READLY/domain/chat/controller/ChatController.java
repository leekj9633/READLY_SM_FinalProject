package com.tricode.READLY.domain.chat.controller;

import com.tricode.READLY.domain.chat.dto.ChatDto;
import com.tricode.READLY.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Value("${ai.api-key}")
    private String aiApiKey;

    /**
     * AI 에이전트가 답변을 보낼 때 사용하는 REST API.
     * AI 서버는 로그인 사용자가 아니라 JWT가 없으므로, 약속된 API 키 헤더로 확인한다.
     * 보낸 사람은 항상 AI로 고정하므로, 이 API로는 다른 회원인 척할 수 없다.
     */
    @PostMapping("/api/book-clubs/{clubId}/chats")
    public ResponseEntity<Void> sendChatMessageFromAi(
            @PathVariable Long clubId,
            @RequestHeader(value = "X-AI-API-KEY", required = false) String apiKey,
            @RequestBody ChatDto.MessageRequest request) {

        if (!aiApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        chatService.sendMessage(clubId, ChatService.AI_MEMBER_ID, request.content());
        return ResponseEntity.ok().build();
    }

    /**
     * 일반 사용자들이 실시간으로 채팅을 보낼 때 사용하는 STOMP 엔드포인트.
     * 보낸 사람 ID는 요청 본문이 아니라 WebSocket 연결 시 검증된 토큰에서 가져온다.
     */
    @MessageMapping("/chat/clubs/{clubId}")
    public void sendWebSocketMessage(
            @DestinationVariable Long clubId,
            @Payload ChatDto.MessageRequest request,
            Principal principal) {

        Long memberId = (Long) ((Authentication) principal).getPrincipal();
        chatService.sendMessage(clubId, memberId, request.content());
    }

    /**
     * 해당 북클럽의 최근 대화(Redis)를 모아 AI에게 개입(assist)을 요청하는 REST API
     */
    @PostMapping("/api/book-clubs/{clubId}/meeting/assist")
    public ResponseEntity<Void> requestMeetingAssist(
            @PathVariable Long clubId,
            @AuthenticationPrincipal Long memberId) {
        chatService.requestMeetingAssist(clubId, memberId);
        return ResponseEntity.ok().build();
    }
}
