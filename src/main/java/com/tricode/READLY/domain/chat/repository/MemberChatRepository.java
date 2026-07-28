package com.tricode.READLY.domain.chat.repository;

import com.tricode.READLY.domain.chat.entity.MemberChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberChatRepository extends JpaRepository<MemberChat, Long> {

    List<MemberChat> findByMemberId(Long memberId);
}
