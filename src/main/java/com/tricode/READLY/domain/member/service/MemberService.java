package com.tricode.READLY.domain.member.service;

import com.tricode.READLY.domain.member.dto.MemberDto;
import com.tricode.READLY.domain.member.entity.Follow;
import com.tricode.READLY.domain.member.entity.Member;
import com.tricode.READLY.domain.member.jwt.JwtTokenProvider;
import com.tricode.READLY.domain.member.repository.FollowRepository;
import com.tricode.READLY.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에서 등록한 BCrypt 빈이 주입

    /**
     * 회원가입
     */
    @Transactional
    public Long signUp(MemberDto.SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = Member.builder()
                .loginId(request.loginId())
                .nickname(request.loginId()) // 닉네임 초기값은 아이디로, 이후 프로필에서 수정 가능
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))   // 평문 비밀번호를 BCrypt 해시 알고리즘으로 암호화하여 저장
                .build();

        return memberRepository.save(member).getId();
    }

    /**
     * 로그인
     */
    public MemberDto.TokenResponse login(MemberDto.LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // passwordEncoder.matches(평문, 암호화된문자열)을 사용하여 일치 여부를 확인합니다.
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호가 일치하면 JWT 토큰을 생성하여 반환합니다.
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getId());

        return new MemberDto.TokenResponse(member.getId(), token);
    }

    /**
     * 내 프로필 조회 (마이페이지)
     */
    public MemberDto.ProfileResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return new MemberDto.ProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getEmail(),
                member.getIntroduction(),
                followRepository.countByFollowingId(memberId),
                followRepository.countByFollowerId(memberId)
        );
    }

    /**
     * 다른 회원의 프로필 조회.
     * 이메일은 내려주지 않고, 대신 요청한 회원이 이미 팔로우 중인지를 함께 알려준다.
     */
    public MemberDto.OtherProfileResponse getOtherProfile(Long targetMemberId, Long myMemberId) {
        Member member = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return new MemberDto.OtherProfileResponse(
                member.getId(),
                member.getNickname(),
                member.getIntroduction(),
                followRepository.countByFollowingId(targetMemberId),
                followRepository.countByFollowerId(targetMemberId),
                followRepository.existsByFollowerIdAndFollowingId(myMemberId, targetMemberId)
        );
    }

    /**
     * 팔로워 목록 확인 (나를 팔로우 하는 사람)
     */
    public List<MemberDto.FollowListResponse> getFollowers(Long memberId) {
        return followRepository.findAllByFollowingIdWithFollower(memberId).stream()
                .map(follow -> new MemberDto.FollowListResponse(
                        follow.getFollower().getId(),
                        follow.getFollower().getNickname(),
                        follow.getFollower().getIntroduction()
                )).collect(Collectors.toList());
    }

    /**
     * 팔로잉 목록 확인 (내가 팔로우 하는 사람)
     */
    public List<MemberDto.FollowListResponse> getFollowings(Long memberId) {
        return followRepository.findAllByFollowerIdWithFollowing(memberId).stream()
                .map(follow -> new MemberDto.FollowListResponse(
                        follow.getFollowing().getId(),
                        follow.getFollowing().getNickname(),
                        follow.getFollowing().getIntroduction()
                )).collect(Collectors.toList());
    }

    /**
     * 다른 사용자 팔로우 하기
     */
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalStateException("이미 팔로우 중인 사용자입니다.");
        }

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우할 회원을 찾을 수 없습니다."));

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // 양쪽 카운터 갱신 (더티 체킹)
        follower.increaseFollowingCount();
        following.increaseFollowerCount();
    }

    /**
     * 팔로우 취소하기.
     * 팔로우와 마찬가지로 취소하는 주체는 파라미터가 아니라 토큰에서 온다.
     */
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new IllegalStateException("팔로우하지 않은 사용자입니다."));

        followRepository.delete(follow);

        // 양쪽 카운터 갱신 (더티 체킹). 프로필 응답은 실제 행을 세지만,
        // 카운터 컬럼도 계속 유지해 두 값이 더 벌어지지 않게 한다.
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우를 취소할 회원을 찾을 수 없습니다."));

        follower.decreaseFollowingCount();
        following.decreaseFollowerCount();
    }

    /**
     * 프로필 수정 (닉네임, 소개글) — 로그인 아이디는 여기서 바꾸지 않는다
     */
    @Transactional
    public void updateProfile(Long memberId, MemberDto.UpdateProfileRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.updateProfile(request.nickname(), request.introduction()); // 더티 체킹으로 반영
    }
}
