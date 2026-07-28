package com.tricode.READLY.domain.chat.entity;

import com.tricode.READLY.domain.book.entity.BookClub;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Chat - BookClub 매핑 (특정 메시지가 어느 북클럽에 속하는지)
@Entity
@Getter
@NoArgsConstructor
public class ChatBookclub {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatMessage chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private BookClub bookClub;
}
