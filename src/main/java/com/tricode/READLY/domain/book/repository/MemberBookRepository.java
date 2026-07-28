package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.MemberBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBookRepository extends JpaRepository<MemberBook, Long> {

    @Query("select mb from MemberBook mb join fetch mb.book where mb.member.id = :memberId")
    List<MemberBook> findAllByMemberIdWithBook(@Param("memberId") Long memberId);
}
