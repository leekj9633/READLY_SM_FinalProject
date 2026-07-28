package com.tricode.READLY.domain.book.controller;

import com.tricode.READLY.domain.book.dto.BookDto;
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
    @GetMapping("/popular") // 엔드포인트 이름이 겹치지 않게 /book 에서 /popular 로 임의 수정 제안
    public ResponseEntity<BookDto.PopularResponse> getPopularBook() { // 수정된 부분
        var response = bookService.getMostPopularBook();
        return ResponseEntity.ok(response);
    }

    /**
     * 기능: 마이페이지에서 내가 읽은 책 목록 확인하기
     */
    @GetMapping("/my-list/{memberId}")
    public ResponseEntity<List<BookDto.MyListResponse>> getMyReadBooks(@PathVariable Long memberId) { // 수정된 부분
        var response = bookService.getMyReadBooks(memberId);
        return ResponseEntity.ok(response);
    }
}