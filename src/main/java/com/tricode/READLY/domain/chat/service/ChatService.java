package com.tricode.READLY.domain.chat.service;

import com.tricode.READLY.domain.book.repository.MemberBookClubRepository;
import com.tricode.READLY.domain.chat.dto.ChatDto;
import com.tricode.READLY.domain.chat.entity.ChatMessage;
import com.tricode.READLY.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    // 채팅에 참여하는 AI 에이전트의 예약된 회원 ID
    public static final Long AI_MEMBER_ID = 999L;

    private final ChatProducer chatProducer;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberBookClubRepository memberBookClubRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    private static final int RECENT_MESSAGE_LIMIT = 50; // AI에게 넘길 최근 대화 개수 제한

    public void sendMessage(Long clubId, Long memberId, String content) {
        validateClubMember(clubId, memberId);

        // Redis에 들어갈 객체 조립 (ID는 UUID로 생성)
        ChatMessage message = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .clubId(clubId)
                .memberId(memberId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        // Kafka로 메시지 발행
        chatProducer.sendMessage(message);
    }

    /**
     * 해당 북클럽의 최근 대화(Redis)를 모아 AI 서버에 개입(assist)을 요청
     */
    public void requestMeetingAssist(Long clubId, Long memberId) {
        validateClubMember(clubId, memberId);

        // createdAt이 비어 있는 과거 데이터가 섞여 있어도 정렬이 깨지지 않도록 nullsLast 사용
        List<ChatMessage> messages = chatMessageRepository.findByClubId(clubId).stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        List<ChatDto.ChatLog> recentLogs = messages.stream()
                .skip(Math.max(0, messages.size() - RECENT_MESSAGE_LIMIT))
                .map(message -> new ChatDto.ChatLog(message.getMemberId(), message.getContent(), message.getCreatedAt()))
                .toList();

        ChatDto.MeetingAssistRequest requestBody = new ChatDto.MeetingAssistRequest(clubId, recentLogs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChatDto.MeetingAssistRequest> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(aiBaseUrl + "/api/meeting/assist", requestEntity, String.class);
            log.info("AI 에이전트에게 대화 개입 요청 성공 (clubId: {}, messageCount: {})", clubId, recentLogs.size());
        } catch (Exception e) {
            log.error("AI 에이전트에게 대화 개입 요청 실패 (clubId: {}): ", clubId, e);
        }
    }

    // 가입하지 않은 북클럽의 채팅방은 이용할 수 없다 (AI 에이전트는 예외)
    private void validateClubMember(Long clubId, Long memberId) {
        if (AI_MEMBER_ID.equals(memberId)) {
            return;
        }
        if (!memberBookClubRepository.existsByMemberIdAndBookClubId(memberId, clubId)) {
            throw new IllegalStateException("가입하지 않은 독서모임입니다.");
        }
    }
}
