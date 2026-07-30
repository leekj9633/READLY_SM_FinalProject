package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.BookClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookClubRepository extends JpaRepository<BookClub, Long> {
    // 기본적인 CRUD 및 findAll() 메서드는 JpaRepository가 자동으로 제공
    // 추가적인 사용자 정의 쿼리가 필요하다면 이곳에 메서드를 선언
}