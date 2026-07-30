package com.tricode.READLY.domain.book.dto;

import com.tricode.READLY.domain.book.entity.BookClub;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookClubDto {

    // 홈화면 독서모임 목록 응답용
    public record HomeListResponse(
            String name,
            LocalDate date,
            LocalTime time,
            int currentMemberCount,
            int maxCapacity,
            BookClub.ClubStatus status,
            BookClub.PassionType type
    ) {}

    // 독서모임 생성 요청용
    public record CreateRequest(
            String name,
            Long bookId, // 책 제목 선택 시 전달받을 책의 PK
            LocalDate date,
            LocalTime time,
            int maxCapacity,
            BookClub.PassionType type
    ) {}
}
