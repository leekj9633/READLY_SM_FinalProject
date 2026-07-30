package com.tricode.READLY.domain.chat.service;

import com.tricode.READLY.domain.chat.entity.ChatMessage;
// import com.tricode.READLY.domain.chat.repository.ChatMessageRepository; // Redis Repository
import com.tricode.READLY.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final Long AI_MEMBER_ID = 999L;
    private static final String AI_SERVER_URL = "http://localhost:8001/api/ai/chat"; // AI 팀원과 협의할 엔드포인트

    @KafkaListener(topics = "chat-group", groupId = "reading-group")
    public void consume(ChatMessage message) {
        log.info("Kafka로부터 수신된 메세지: {}", message.getContent());

        // 1. Redis에 저장 (기존 로직)
        chatMessageRepository.save(message);

        // 2. WebSocket 구독자들에게 실시간 브로드캐스팅 (필수!)
        // "/sub/chat/clubs/{clubId}" 를 구독하고 있는 클라이언트들에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/clubs/" + message.getClubId(), message);

        // 3. AI 에이전트에게 메시지 전달 (AI가 보낸 메시지가 아닐 때만)
        if (!AI_MEMBER_ID.equals(message.getMemberId())) {
            sendToAiAgent(message);
        }
    }

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

            // 8001번 포트로 POST 요청 전송
            restTemplate.postForEntity(AI_SERVER_URL, requestEntity, String.class);
            log.info("AI 에이전트로 메시지 전송 성공 (clubId: {})", message.getClubId());

        } catch (Exception e) {
            log.error("AI 에이전트로 메시지 전송 실패: ", e);
        }
    }

    // AI 서버로 보낼 JSON DTO (record 사용)
    public record AiMessageRequest(Long clubId, Long memberId, String content) {}
}
