package com.tricode.READLY.domain.member.dto;

public class MemberDto {

    // [기능 2] 회원가입 요청
    public record SignUpRequest(
            String name,
            String email,
            String password
    ) {}

    // [기능 3] 로그인 요청
    public record LoginRequest(
            String email,
            String password
    ) {}

    // [기능 4] 팔로우/팔로잉 목록 응답
    public record FollowListResponse(
            Long memberId,
            String name,
            String introduction
    ) {}

    // [기능 8] 프로필 수정 요청
    public record UpdateProfileRequest(
            String name,
            String introduction
    ) {}

    // 프로필 정보 응답 (공통)
    public record ProfileResponse(
            Long memberId,
            String name,
            String email,
            String introduction,
            int followerCount,
            int followingCount
    ) {}
}
