package com.tricode.READLY.domain.chat.controller;

import com.tricode.READLY.domain.book.service.BookClubService;
import com.tricode.READLY.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // AI 에이전트가 응답을 보낼 때 주로 사용할 REST API
    @PostMapping("/api/book-clubs/{clubId}/chats")
    public ResponseEntity<Void> sendChatMessage(
            @PathVariable Long clubId,
            @RequestBody ChatMessageRequest request) {

        chatService.sendMessage(clubId, request.memberId(), request.content());
        return ResponseEntity.ok().build();
    }

    // 일반 사용자들이 실시간으로 채팅을 보낼 때 사용하는 STOMP 엔드포인트
    @MessageMapping("/chat/clubs/{clubId}")
    public void sendWebSocketMessage(
            @DestinationVariable Long clubId,
            @Payload ChatMessageRequest request) {

        chatService.sendMessage(clubId, request.memberId(), request.content());
    }

    public record ChatMessageRequest(Long memberId, String content) {}
}
