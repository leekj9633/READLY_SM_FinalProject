package com.tricode.READLY.domain.chat.dto;

public class ChatDto {

    // [기능 6] 채팅 전송 요청용
    public record MessageRequest(
            Long memberId,
            String content
    ) {}
}
