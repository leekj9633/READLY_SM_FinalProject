package com.tricode.READLY.domain.book.entity;

import com.tricode.READLY.domain.member.entity.Member;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Member - Book 매핑 (예: 회원이 찜한 책, 읽은 책 등)
@Entity
@Getter
@NoArgsConstructor
public class MemberBook {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;
}
