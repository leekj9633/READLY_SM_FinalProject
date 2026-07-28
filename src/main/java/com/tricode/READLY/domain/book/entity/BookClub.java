package com.tricode.READLY.domain.book.entity;

import com.tricode.READLY.domain.chat.entity.ChatBookclub;
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
    private List<MemberBookclub> memberBookClubs = new ArrayList<>();

    @OneToMany(mappedBy = "bookClub")
    private List<ChatBookclub> chatBookClubs = new ArrayList<>();

}
