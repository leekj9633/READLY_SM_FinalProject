package com.tricode.READLY.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MemberDto {

    // 회원가입 요청
    // 검증이 없으면 값이 비어 있을 때 DB NOT NULL 제약에 걸려 500으로 나간다.
    // 사용자 잘못은 400으로 돌려주고 어떤 필드가 문제인지 알려준다.
    public record SignUpRequest(
            @NotBlank(message = "아이디는 필수입니다.")
            String loginId,

            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}

    // 로그인 요청
    public record LoginRequest(
            @NotBlank(message = "이메일은 필수입니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
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
