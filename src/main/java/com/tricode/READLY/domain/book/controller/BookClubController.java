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
     * 기능: 홈화면에서 독서모임 정보 리스트 보기.
     * 가입하지 않은 모임도 함께 내려가며, role은 내가 가입한 모임에만 값이 들어간다.
     * 프론트가 이 값으로 "가입 여부"를 판단해 입장 전에 join을 호출할지 정한다.
     */
    @GetMapping
    public ResponseEntity<List<BookClubDto.HomeListResponse>> getHomeBookClubs(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(bookClubService.getHomeBookClubs(memberId));
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
     * 기능: 독서모임 상세 정보 보기 (방 입장).
     * 응답의 role(HOST/PARTICIPANT)로 프론트가 방장 전용 UI 노출을 판단한다.
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<BookClubDto.DetailResponse> getBookClubDetail(
            @PathVariable Long clubId,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(bookClubService.getBookClubDetail(clubId, memberId));
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
