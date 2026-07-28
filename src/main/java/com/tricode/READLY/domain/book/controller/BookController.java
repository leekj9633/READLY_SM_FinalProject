package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * 기능: 홈화면에서 가장 인기가 많은 책의 제목과 커버 이미지 보여주기
     */
    @GetMapping("/book")      // 수정 필요
    public ResponseEntity<PopularBookResponse> getPopularBook() {
        // Service에서 DTO를 반환한다고 가정
        var response = bookService.getMostPopularBook();
        return ResponseEntity.ok(response);
    }

    /**
     * 기능: 마이페이지에서 내가 읽은 책 목록 확인하기
     * 실제 환경에서는 PathVariable 대신 @AuthenticationPrincipal 등으로 로그인한 유저 ID를 가져옴
     */
    @GetMapping("/my-list/{memberId}")      // 수정 필요
    public ResponseEntity<List<MyBookListResponse>> getMyReadBooks(@PathVariable Long memberId) {
        var response = bookService.getMyReadBooks(memberId);
        return ResponseEntity.ok(response);
    }

    // --- DTO ---
    public record PopularBookResponse(String name, String coverImageUrl) {}
    public record MyBookListResponse(Long bookId, String name, String coverImageUrl) {}
}
