package com.tricode.READLY.domain.member.controller;

import com.tricode.READLY.domain.member.dto.MemberDto;
import com.tricode.READLY.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // [기능 2] 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody MemberDto.SignUpRequest request) {
        return ResponseEntity.ok(memberService.signUp(request));
    }

    // [기능 3] 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberDto.TokenResponse> login(@RequestBody MemberDto.LoginRequest request) {
        // 성공 시 200 OK와 함께 { "memberId": 1, "accessToken": "eyJhbGci..." } 형태의 JSON이 반환
        return ResponseEntity.ok(memberService.login(request));
    }

    // 마이페이지: 내 프로필 조회
    // "/me"는 리터럴이라 아래 "/{memberId}" 매핑보다 먼저 매칭된다
    @GetMapping("/me")
    public ResponseEntity<MemberDto.ProfileResponse> getMyProfile(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getMyProfile(memberId));
    }

    // 다른 회원의 프로필 조회 (이메일은 내려주지 않고, 내가 팔로우 중인지 여부를 함께 준다)
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto.OtherProfileResponse> getOtherProfile(
            @PathVariable Long memberId,
            @AuthenticationPrincipal Long myMemberId) {
        return ResponseEntity.ok(memberService.getOtherProfile(memberId, myMemberId));
    }

    // [기능 4] 팔로워 목록 (나를 팔로우 하는 사람)
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<List<MemberDto.FollowListResponse>> getFollowers(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getFollowers(memberId));
    }

    // [기능 4] 팔로잉 목록 (내가 팔로우 하는 사람)
    @GetMapping("/{memberId}/followings")
    public ResponseEntity<List<MemberDto.FollowListResponse>> getFollowings(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getFollowings(memberId));
    }

    // [기능 7] 다른 사용자 팔로우 (ex: BookClub에서 프로필 누르고 팔로우)
    // 팔로우하는 주체는 요청 파라미터가 아니라 토큰에서 가져온다 (남의 이름으로 팔로우 방지)
    @PostMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal Long myMemberId) {
        memberService.followUser(myMemberId, followingId);
        return ResponseEntity.ok().build();
    }

    // 팔로우 취소 (팔로우와 같은 경로, 메서드만 DELETE)
    @DeleteMapping("/{followingId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal Long myMemberId) {
        memberService.unfollowUser(myMemberId, followingId);
        return ResponseEntity.ok().build();
    }

    // [기능 8] 마이페이지 프로필 수정 (본인 것만 수정 가능)
    @PatchMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MemberDto.UpdateProfileRequest request) {
        memberService.updateProfile(memberId, request);
        return ResponseEntity.ok().build();
    }
}
