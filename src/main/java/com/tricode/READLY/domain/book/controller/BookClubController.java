package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.dto.BookClubDto;
import com.tricode.READLY.domain.book.service.BookClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-clubs")
public class BookClubController {

    private final BookClubService bookClubService;

    /**
     * 기능: 홈화면에서 독서모임 정보 리스트 보기
     */
    @GetMapping
    public ResponseEntity<List<BookClubDto.HomeListResponse>> getHomeBookClubs() {
        return ResponseEntity.ok(bookClubService.getHomeBookClubs());
    }

    /**
     * 기능: 내가 가입한 독서모임 목록 보기
     */
    @GetMapping("/my-list")
    public ResponseEntity<List<BookClubDto.HomeListResponse>> getMyBookClubs(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(bookClubService.getMyBookClubs(memberId));
    }

    /**
     * 기능: 독서모임 만들기 (만든 사람은 자동 가입)
     */
    @PostMapping
    public ResponseEntity<Long> createBookClub(
            @AuthenticationPrincipal Long memberId,
            @RequestBody BookClubDto.CreateRequest request) {
        return ResponseEntity.ok(bookClubService.createBookClub(memberId, request));
    }

    /**
     * 기능: 독서모임 가입하기
     */
    @PostMapping("/{clubId}/join")
    public ResponseEntity<Void> joinBookClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal Long memberId) {
        bookClubService.joinBookClub(clubId, memberId);
        return ResponseEntity.ok().build();
    }
}
