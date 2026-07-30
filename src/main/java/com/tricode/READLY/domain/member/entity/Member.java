package com.tricode.READLY.domain.member.entity;

import com.tricode.READLY.domain.book.entity.MemberBook;
import com.tricode.READLY.domain.book.entity.MemberBookClub;
import com.tricode.READLY.domain.chat.entity.MemberChat;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;    // 사용자의 ID

    @Column(nullable = false, unique = true)
    private String email;   // 사용자의 이메일

    @Column(nullable = false)
    private String password;    // 사용자의 비밀번호

    private String introduction;    // 사용자의 소개글

    // 팔로워/팔로잉 수는 매번 계산하거나 별도 배치로 업데이트하는 것을 권장합니다.
    // 만약 조회가 잦아 성능 최적화가 필요하다면 아래 컬럼의 주석을 해제하세요.
     private int followerCount;
     private int followingCount;

    // 양방향 매핑 (필요한 경우에만 선언)
    @OneToMany(mappedBy = "member")
    private List<MemberBook> memberBooks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberBookClub> memberBookClubs = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberChat> memberChats = new ArrayList<>();
}

