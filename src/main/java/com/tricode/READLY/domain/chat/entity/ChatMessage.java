package com.tricode.READLY.domain.chat.entity;

import jakarta.persistence.OneToMany;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// Redis에 데이터가 해시(Hash) 형태로 저장될 Key의 Prefix와 만료 시간(TTL, 초 단위)을 설정
@RedisHash(value = "chat", timeToLive = 604800) // timeToLive 속성으로 채팅 데이터가 캐시 메모리에 너무 오래 쌓이지 않도록 7일(604800초) 동안 보관
public class ChatMessage {
    // 이 ChatMessage 객체는 유저가 메세지를 보낼 때 Kafka의 Producer를 통해 토픽(Topic)으로 발행(Publish)되고,
    // Consumer가 이를 읽어들여 Redis에 위 엔티티 형태로 저장(Save)하는 흐름으로 구현

    @Id
    private String id; // Redis의 PK (보통 UUID 등으로 자동 생성하여 사용)

    @Indexed // Redis는 Key-Value 저장소로 조건 검색 어려워 clubId에 @Indexed를 붙여, 특정 채팅방(clubId)에 해당하는 메세지 목록만 빠르게 필터링해서 불러오기
    private Long clubId;

    private Long memberId;

    private String content;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatMessage")
    private List<MemberChat> memberChats = new ArrayList<>();

    @OneToMany(mappedBy = "chatMessage")
    private List<ChatBookclub> chatBookClubs = new ArrayList<>();
}
