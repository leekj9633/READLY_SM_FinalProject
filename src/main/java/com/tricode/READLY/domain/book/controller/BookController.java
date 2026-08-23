package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.dto.BookDto;
import com.tricode.READLY.domain.book.service.BookService;
import jakarta.validation.Valid;
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
     * 기능: 다른 회원의 프로필에서 그 사람이 읽은 책 목록 확인하기.
     * 응답 형식은 내 목록(/my-list)과 같고, 조회 대상만 다르다.
     */
    @GetMapping("/members/{memberId}/list")
    public ResponseEntity<List<BookDto.MyListResponse>> getMemberReadBooks(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(bookService.getMyReadBooks(memberId));
    }

    /**
     * 기능: 제목으로 책 검색하기 (알라딘 ItemSearch).
     * 검색 단계에서는 아무것도 저장하지 않는다. 응답의 isbn13을 그대로 등록 요청에 실어 보내면 된다.
     */
    // keyword를 required=false로 받는 이유: 필수로 두면 파라미터 자체가 빠졌을 때
    // MissingServletRequestParameterException이 나고, 전용 핸들러가 없어 500으로 나간다.
    // null을 그대로 서비스로 넘겨 빈 검색어와 같은 400("검색어를 입력해 주세요.")으로 통일한다.
    @GetMapping("/search")
    public ResponseEntity<List<BookDto.SearchResponse>> searchBooks(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    /**
     * 기능: 책 등록하기 (검색 결과에서 고른 책을 우리 DB에 저장).
     * 수기 입력은 받지 않으므로 요청 본문은 isbn13 하나뿐이고, 비어 있으면 400이다.
     */
    @PostMapping
    public ResponseEntity<Long> registerBook(@Valid @RequestBody BookDto.CreateRequest request) {
        return ResponseEntity.ok(bookService.registerBook(request));
    }

    /**
     * 기능: ISBN13으로 책 조회 (DB에 없으면 알라딘 API로 가져와 저장한다)
     */
    @GetMapping("/isbn/{isbn13}")
    public ResponseEntity<BookDto.IsbnResponse> getBookByIsbn(@PathVariable String isbn13) {
        return ResponseEntity.ok(BookDto.IsbnResponse.from(bookService.getOrCreateBookByIsbn13(isbn13)));
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
