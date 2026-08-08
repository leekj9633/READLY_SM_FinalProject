package com.tricode.READLY.domain.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDto {

    // [기능 6] 채팅 전송 요청용
    // 보낸 사람(memberId)은 본문으로 받지 않는다.
    // 사용자는 WebSocket 연결 시 검증된 토큰에서, AI는 서버가 고정값으로 넣는다.
    public record MessageRequest(
            String content
    ) {}

    // AI 서버의 /api/meeting/assist 로 보낼 대화 개입 요청
    public record MeetingAssistRequest(
            Long clubId,
            List<ChatLog> messages
    ) {}

    public record ChatLog(
            Long memberId,
            String content,
            LocalDateTime createdAt
    ) {}
}
