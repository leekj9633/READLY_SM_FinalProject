package com.tricode.READLY.domain.member.controller;

import com.tricode.READLY.domain.member.dto.MemberDto;
import com.tricode.READLY.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> login(@RequestBody MemberDto.LoginRequest request) {
        return ResponseEntity.ok(memberService.login(request));
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
    @PostMapping("/{followingId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long followingId,
            @RequestParam Long myMemberId) { // 실제로는 세션이나 토큰에서 내 ID를 가져옴
        memberService.followUser(myMemberId, followingId);
        return ResponseEntity.ok().build();
    }

    // [기능 8] 마이페이지 프로필 수정
    @PatchMapping("/{memberId}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable Long memberId,
            @RequestBody MemberDto.UpdateProfileRequest request) {
        memberService.updateProfile(memberId, request);
        return ResponseEntity.ok().build();
    }
}
