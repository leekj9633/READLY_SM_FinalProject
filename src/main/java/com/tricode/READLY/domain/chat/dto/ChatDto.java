package com.tricode.READLY.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    // 모임장이 AI 진행자 개입 버튼을 누를 때 보내는 요청
    public record AiAssistRequest(
            String mode // "question"(토론 질문 제안) 또는 "summary"(대화 요약)
    ) {}

    // AI 서버 POST /api/meeting/assist 요청 바디
    public record MeetingAssistApiRequest(
            @JsonProperty("book_title") String bookTitle,
            @JsonProperty("chat_history") List<AiChatHistoryItem> chatHistory,
            String mode
    ) {}

    public record AiChatHistoryItem(
            String speaker,
            String text
    ) {}

    // AI 서버 POST /api/meeting/assist 응답 바디
    public record MeetingAssistApiResponse(
            String mode,
            String result
    ) {}
}
