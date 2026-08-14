package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.BookClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookClubRepository extends JpaRepository<BookClub, Long> {

    // 목록 조회 시 책 정보를 함께 가져온다 (책마다 쿼리가 또 나가는 것을 방지)
    @Query("select bc from BookClub bc left join fetch bc.book")
    List<BookClub> findAllWithBook();

    // AI 개입 요청 시 책 제목이 필요해 book을 함께 가져온다
    @Query("select bc from BookClub bc left join fetch bc.book where bc.id = :clubId")
    Optional<BookClub> findByIdWithBook(@Param("clubId") Long clubId);
}