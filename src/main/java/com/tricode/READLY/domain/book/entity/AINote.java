package com.tricode.READLY.domain.book.entity;

import com.tricode.READLY.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

// 한 회원이 특정 책에 대해 남긴 여러 BookNote를 AI가 취합해 만든 통합 독서록.
// 책 1권 + 회원 1명당 하나만 존재하며, 회원이 내용을 수정할 수 있다.
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "member_id"}))
public class AINote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_note_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String content; // AI가 생성한 통합 독서록 내용

    // AI 원본 그대로인지, 회원이 손을 댔는지 구분
    // (AINote는 전부 AI가 생성하므로 isAiGenerated는 항상 true라 의미가 없음)
    @Column(nullable = false)
    private boolean edited;

    // AI가 새로 생성한 내용으로 채우기 (재생성 시 회원 수정분은 덮어써짐)
    public void applyAiContent(String content) {
        this.content = content;
        this.edited = false;
    }

    // 회원이 AI 독서록 내용을 직접 수정 (더티 체킹)
    public void editContent(String content) {
        this.content = content;
        this.edited = true;
    }
}