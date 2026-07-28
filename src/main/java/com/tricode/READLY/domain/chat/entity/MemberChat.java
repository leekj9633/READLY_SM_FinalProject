package com.tricode.READLY.domain.chat.entity;

import com.tricode.READLY.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Member - Chat 매핑 (예: 특정 메시지의 수신/발신자, 읽음 여부 등)
@Entity
@Getter
@NoArgsConstructor
public class MemberChat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatMessage chat;
}
