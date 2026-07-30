package com.tricode.READLY.domain.chat.controller;

import com.tricode.READLY.domain.book.service.BookClubService;
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

    private final BookClubService bookClubService;

    /**
     * 1. REST API 방식) 외부 API나 단순 HTTP 요청으로 채팅을 보낼 때 사용
     */
    @PostMapping("/api/book-clubs/{clubId}/chats")
    public ResponseEntity<Void> sendChatMessage(
            @PathVariable Long clubId,
            @RequestBody ChatMessageRequest request) {

        bookClubService.sendMessageToBookClub(clubId, request.memberId(), request.content());
        return ResponseEntity.ok().build();
    }

    /**
     * 2. WebSocket/STOMP 방식: 실제 채팅방 구현 시 사용하는 방식
     * 클라이언트가 "/pub/chat/clubs/{clubId}" 경로로 메시지를 쏘면 이 메서드가 받습니다.
     */
    @MessageMapping("/chat/clubs/{clubId}")
    public void sendWebSocketMessage(
            @DestinationVariable Long clubId,
            @Payload ChatMessageRequest request) {

        // Kafka Producer를 통해 메시지 발행
        bookClubService.sendMessageToBookClub(clubId, request.memberId(), request.content());
    }

    // --- DTO ---
    public record ChatMessageRequest(Long memberId, String content) {}
}
