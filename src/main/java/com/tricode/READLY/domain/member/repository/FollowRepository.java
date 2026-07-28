package com.tricode.READLY.domain.member.repository;

import com.tricode.READLY.domain.member.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 내가 팔로우 하는 사람들 (Following)
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower.id = :memberId")
    List<Follow> findAllByFollowerIdWithFollowing(@Param("memberId") Long memberId);

    // 나를 팔로우 하는 사람들 (Follower)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following.id = :memberId")
    List<Follow> findAllByFollowingIdWithFollower(@Param("memberId") Long memberId);

    // 이미 팔로우 중인지 확인
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
