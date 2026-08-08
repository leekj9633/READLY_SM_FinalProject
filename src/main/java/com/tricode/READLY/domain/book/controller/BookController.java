package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.dto.BookDto;
import com.tricode.READLY.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * 기능: 홈화면에서 가장 인기가 많은 책의 제목과 커버 이미지 보여주기
     */
    @GetMapping("/popular")
    public ResponseEntity<BookDto.PopularResponse> getPopularBook() {
        return ResponseEntity.ok(bookService.getMostPopularBook());
    }

    /**
     * 기능: 마이페이지에서 내가 읽은 책 목록 확인하기
     */
    @GetMapping("/my-list")
    public ResponseEntity<List<BookDto.MyListResponse>> getMyReadBooks(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(bookService.getMyReadBooks(memberId));
    }

    /**
     * 기능: 책 등록하기 (외부 도서 API에서 검색한 책을 우리 DB에 저장)
     */
    @PostMapping
    public ResponseEntity<Long> registerBook(@RequestBody BookDto.CreateRequest request) {
        return ResponseEntity.ok(bookService.registerBook(request));
    }

    /**
     * 기능: 내가 읽은 책 목록에 책 추가하기
     */
    @PostMapping("/{bookId}/my-list")
    public ResponseEntity<Void> addBookToMyList(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId) {
        bookService.addBookToMyList(bookId, memberId);
        return ResponseEntity.ok().build();
    }
}
