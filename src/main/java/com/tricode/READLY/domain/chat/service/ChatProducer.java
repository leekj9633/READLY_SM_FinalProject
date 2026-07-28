package com.tricode.READLY.domain.chat.service;

import com.tricode.READLY.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatProducer {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    private static final String TOPIC = "chat-group"; // Kafka 토픽 이름

    public void sendMessage(ChatMessage message) {
        // ChatMessage 엔티티 객체를 Kafka 토픽으로 전송하여 연계 시작!
        kafkaTemplate.send(TOPIC, message);
    }
}
