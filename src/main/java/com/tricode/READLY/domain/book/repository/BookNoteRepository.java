package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.BookNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookNoteRepository extends JpaRepository<BookNote, Long> {

    // 특정 회원이 특정 책에 대해 작성한 모든 독서록 목록 조회
    @Query("SELECT bn FROM BookNote bn WHERE bn.book.id = :bookId AND bn.member.id = :memberId")
    List<BookNote> findAllByBookIdAndMemberId(@Param("bookId") Long bookId, @Param("memberId") Long memberId);

    // 내가 독서록을 작성한 '책'의 목록만 중복 없이 조회
    @Query("SELECT DISTINCT bn.book FROM BookNote bn WHERE bn.member.id = :memberId")
    List<Book> findBooksWithMyNotes(@Param("memberId") Long memberId);
}
