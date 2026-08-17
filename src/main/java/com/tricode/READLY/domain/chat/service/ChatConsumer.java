package com.tricode.READLY.domain.chat.service;

import com.tricode.READLY.domain.chat.entity.ChatMessage;
// import com.tricode.READLY.domain.chat.repository.ChatMessageRepository; // Redis Repository
import com.tricode.READLY.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatConsumer {

    private final RestTemplate restTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations messagingTemplate; // 추가됨: STOMP 브로드캐스팅 객체

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    @KafkaListener(topics = "chat-group", groupId = "reading-group")
    public void consume(ChatMessage message) {
        log.info("Kafka로부터 수신된 메세지: {}", message.getContent());

        // 1. Redis에 저장 (기존 로직)
        chatMessageRepository.save(message);

        // 2. WebSocket 구독자들에게 실시간 브로드캐스팅 (필수!)
        // "/sub/chat/clubs/{clubId}" 를 구독하고 있는 클라이언트들에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/clubs/" + message.getClubId(), message);

        // 3. AI 에이전트에게 메시지 전달 (AI가 보낸 메시지가 아닐 때만)
        if (!ChatService.AI_MEMBER_ID.equals(message.getMemberId())) {
            sendToAiAgent(message);
        }
    }

    // 여기는 Kafka 리스너에서 도는 비동기 경로라 사용자에게 돌려줄 응답이 없다.
    // 이미 Redis 저장과 브로드캐스트는 끝난 뒤이므로, AI 전달 실패는 로그만 남기고 삼킨다.
    // (사용자가 직접 호출하는 ChatService의 assist 경로는 반대로 503을 던진다)
    private void sendToAiAgent(ChatMessage message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // AI 서버로 보낼 JSON 데이터 구조체 생성
            AiMessageRequest requestBody = new AiMessageRequest(
                    message.getClubId(),
                    message.getMemberId(),
                    message.getContent()
            );

            HttpEntity<AiMessageRequest> requestEntity = new HttpEntity<>(requestBody, headers);

            restTemplate.postForEntity(aiBaseUrl + "/api/ai/chat", requestEntity, String.class);
            log.info("AI 에이전트로 메시지 전송 성공 (clubId: {})", message.getClubId());

        } catch (Exception e) {
            log.error("AI 에이전트로 메시지 전송 실패: ", e);
        }
    }

    // AI 서버로 보낼 JSON DTO (record 사용)
    public record AiMessageRequest(Long clubId, Long memberId, String content) {}
}
