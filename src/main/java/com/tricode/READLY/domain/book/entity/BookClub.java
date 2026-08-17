package com.tricode.READLY.domain.book.entity;

import com.tricode.READLY.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    // 이 북클럽이 함께 읽는 책 (책 1권에 여러 북클럽이 생길 수 있다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    // 방장(모임을 만든 회원). 예전에는 member_book_club의 id가 가장 작은 행을 방장으로 봤지만,
    // 그 규칙은 방장이 탈퇴하면 다음 가입자가 조용히 방장이 되는 문제가 있어 컬럼으로 명시한다.
    // FK가 하나뿐이라 "한 모임에 방장은 한 명"이 구조적으로 보장된다.
    // nullable인 이유는 이 컬럼이 생기기 전에 만들어진 모임이 남아 있기 때문이다
    // (db/2026-08-17-add-bookclub-host.sql로 백필한다).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Member host;

    private LocalDate creationDate;
    private LocalTime creationTime;

    private int memberCapacity; // 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassionType type; // 북클럽 타입 (열정도)

    public enum PassionType {
        PASSIONATE,
        MODERATE,
        CALM
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubStatus status; // 시작/진행/종료 여부

    public enum ClubStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    @OneToMany(mappedBy = "bookClub")
    private List<MemberBookClub> memberBookClubs = new ArrayList<>();

}
