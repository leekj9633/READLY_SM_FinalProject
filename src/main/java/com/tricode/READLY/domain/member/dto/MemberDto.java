package com.tricode.READLY.domain.member.dto;

public class MemberDto {

    // 회원가입 요청
    public record SignUpRequest(
            String loginId,
            String email,
            String password
    ) {}

    // 로그인 요청
    public record LoginRequest(
            String email,
            String password
    ) {}

    // JWT 토큰 반환용 DTO 추가
    public record TokenResponse(
            Long memberId,
            String accessToken
    ) {}

    // 팔로우/팔로잉 목록 응답
    public record FollowListResponse(
            Long memberId,
            String nickname,
            String introduction
    ) {}

    // 프로필 수정 요청 (로그인 아이디는 여기서 바꾸지 않는다)
    public record UpdateProfileRequest(
            String nickname,
            String introduction
    ) {}

    // 내 프로필 응답 (GET /api/members/me)
    // followerCount/followingCount는 Member의 카운터 컬럼이 아니라 Follow 행을 실제로 세어 채운다.
    // 카운터는 팔로우 시점에만 증가시켜 온 값이라 과거 데이터가 어긋나 있을 수 있기 때문이다.
    public record ProfileResponse(
            Long memberId,
            String nickname,
            String email,
            String introduction,
            long followerCount,
            long followingCount
    ) {}

    // 타인 프로필 응답 (GET /api/members/{memberId})
    // 남의 이메일은 내려주지 않는다. isFollowing으로 프론트가 팔로우 버튼 상태를 정확히 그릴 수 있다.
    public record OtherProfileResponse(
            Long memberId,
            String nickname,
            String introduction,
            long followerCount,
            long followingCount,
            boolean isFollowing
    ) {}

}
