package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.AINote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AINoteRepository extends JpaRepository<AINote, Long> {

    // 회원이 특정 책에 대해 가진 AI 독서록 (책+회원당 1개)
    Optional<AINote> findByBookIdAndMemberId(Long bookId, Long memberId);
}