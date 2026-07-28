package com.tricode.READLY.domain.book.entity;

import com.tricode.READLY.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Member - BookClub 매핑 (회원의 북클럽 가입 정보)
@Entity
@Getter
@NoArgsConstructor
public class MemberBookclub {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private BookClub bookClub;
}
