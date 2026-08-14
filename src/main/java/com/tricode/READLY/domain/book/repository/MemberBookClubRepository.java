package com.tricode.READLY.domain.book.repository;

import com.tricode.READLY.domain.book.entity.MemberBookClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBookClubRepository extends JpaRepository<MemberBookClub, Long> {

    @Query("select mbc from MemberBookClub mbc join fetch mbc.bookClub where mbc.member.id = :memberId")
    List<MemberBookClub> findAllByMemberIdWithBookClub(@Param("memberId") Long memberId);

    @Query("select mbc from MemberBookClub mbc join fetch mbc.member where mbc.bookClub.id = :clubId")
    List<MemberBookClub> findAllByBookClubIdWithMember(@Param("clubId") Long clubId);

    int countByBookClubId(Long bookClubId);

    // 이미 가입한 북클럽인지 확인
    boolean existsByMemberIdAndBookClubId(Long memberId, Long bookClubId);

    // 모임장 확인용: 가장 먼저 가입한 회원(=클럽 생성자)을 조회 (createBookClub에서 생성자를 가장 먼저 저장하기 때문)
    MemberBookClub findFirstByBookClubIdOrderByIdAsc(Long bookClubId);
}
