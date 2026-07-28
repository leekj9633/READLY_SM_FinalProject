package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.dto.BookclubDto;
import com.tricode.READLY.domain.book.service.BookclubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<BookclubDto.HomeListResponse>> getHomeBookClubs() {
        var response = bookClubService.getHomeBookClubs();
        return ResponseEntity.ok(response);
    }

    /**
     * 기능: 독서모임 만들기
     */
    @PostMapping
    public ResponseEntity<Long> createBookClub(@RequestBody BookclubDto.CreateRequest request) { // 수정된 부분
        Long createdClubId = bookClubService.createBookClub(request);
        return ResponseEntity.ok(createdClubId);
    }
}