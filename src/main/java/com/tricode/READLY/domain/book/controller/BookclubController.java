package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.entity.BookClub;
import com.tricode.READLY.domain.book.service.BookclubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-clubs")
public class BookclubController {

    private final BookclubService bookClubService;

    /**
     * 기능: 홈화면에서 독서모임 정보 리스트 보기
     */
    @GetMapping
    public ResponseEntity<List<HomeBookClubResponse>> getHomeBookClubs() {
        var response = bookClubService.getHomeBookClubs();
        return ResponseEntity.ok(response);
    }

    /**
     * 기능: 독서모임 만들기
     */
    @PostMapping
    public ResponseEntity<Long> createBookClub(@RequestBody CreateBookClubRequest request) {
        Long createdClubId = bookClubService.createBookClub(request);
        return ResponseEntity.ok(createdClubId);
    }

    // --- DTO ---
    public record HomeBookClubResponse(
            String name,
            LocalDate date,
            LocalTime time,
            int currentMemberCount,
            int maxCapacity,
            BookClub.ClubStatus status,
            BookClub.PassionType type
    ) {}

    public record CreateBookClubRequest(
            String name,
            Long bookId, // 책 제목 선택 시 전달받을 ID
            LocalDate date,
            LocalTime time,
            int maxCapacity,
            BookClub.PassionType type
    ) {}
}
