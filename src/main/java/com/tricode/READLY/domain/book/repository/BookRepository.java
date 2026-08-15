package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * [기능 1] 홈화면에서 가장 인기가 많은 책 조회
     * MemberBook(회원이 읽은 책) 매핑 수가 가장 많은 책 1권을 가져옵니다.
     */
    @Query("SELECT b FROM Book b " +
            "LEFT JOIN b.memberBooks mb " +
            "GROUP BY b " +
            "ORDER BY COUNT(mb) DESC LIMIT 1")
    Optional<Book> findTopByOrderByMemberBooksDesc();

    /**
     * 이미 저장된 책인지 확인 (있으면 알라딘 API를 호출하지 않는다)
     */
    Optional<Book> findByIsbn13(String isbn13);
}
