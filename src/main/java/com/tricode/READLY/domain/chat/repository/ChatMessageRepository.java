package com.tricode.READLY.domain.chat.repository;

import com.tricode.READLY.domain.chat.entity.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, String> {
    // 특정 북클럽의 채팅 내역을 조회하기 위한 메서드 (@Indexed가 붙은 clubId 활용)
    List<ChatMessage> findByClubId(Long clubId);
}
