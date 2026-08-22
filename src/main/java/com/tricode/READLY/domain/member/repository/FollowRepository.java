package com.tricode.READLY.domain.member.repository;

import com.tricode.READLY.domain.member.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 내가 팔로우 하는 사람들 (Following)
    @Query("SELECT f FROM Follow f JOIN FETCH f.following WHERE f.follower.id = :memberId")
    List<Follow> findAllByFollowerIdWithFollowing(@Param("memberId") Long memberId);

    // 나를 팔로우 하는 사람들 (Follower)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.following.id = :memberId")
    List<Follow> findAllByFollowingIdWithFollower(@Param("memberId") Long memberId);

    // 이미 팔로우 중인지 확인
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 언팔로우할 때 지울 행을 찾는다 (없으면 팔로우하지 않은 상태)
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    // 프로필에 보여줄 팔로워/팔로잉 수.
    // Member의 카운터 컬럼을 쓰지 않고 실제 행을 세는 이유는 MemberDto.ProfileResponse 주석 참고.
    long countByFollowingId(Long memberId); // 나를 팔로우 하는 사람 수

    long countByFollowerId(Long memberId);  // 내가 팔로우 하는 사람 수
}
