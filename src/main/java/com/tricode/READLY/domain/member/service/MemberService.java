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

        // 엔티티에 name 필드가 추가되었다고 가정
        Member member = Member.builder()
                .name(request.name())
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
     * 팔로워 목록 확인 (나를 팔로우 하는 사람)
     */
    public List<MemberDto.FollowListResponse> getFollowers(Long memberId) {
        return followRepository.findAllByFollowingIdWithFollower(memberId).stream()
                .map(follow -> new MemberDto.FollowListResponse(
                        follow.getFollower().getId(),
                        follow.getFollower().getName(),
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
                        follow.getFollowing().getName(),
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

        Member follower = memberRepository.findById(followerId).orElseThrow();
        Member following = memberRepository.findById(followingId).orElseThrow();

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        // 추가: Member 엔티티의 followerCount, followingCount 증가 로직 (엔티티에 메서드 구현 필요)
    }

    /**
     * 프로필 수정 (이름, 소개글)
     */
    @Transactional
    public void updateProfile(Long memberId, MemberDto.UpdateProfileRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 엔티티에 updateProfile 메서드가 만들어져 있다고 가정 (더티 체킹)
        // member.updateProfile(request.name(), request.introduction());
    }
}
