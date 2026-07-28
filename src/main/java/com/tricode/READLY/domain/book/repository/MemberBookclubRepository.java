package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.MemberBookclub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBookclubRepository extends JpaRepository<MemberBookclub, Long> {

    @Query("select mbc from MemberBookClub mbc join fetch mbc.bookClub where mbc.member.id = :memberId")
    List<MemberBookclub> findAllByMemberIdWithBookClub(@Param("memberId") Long memberId);

    @Query("select mbc from MemberBookClub mbc join fetch mbc.member where mbc.bookClub.id = :clubId")
    List<MemberBookclub> findAllByBookClubIdWithMember(@Param("clubId") Long clubId);
}
