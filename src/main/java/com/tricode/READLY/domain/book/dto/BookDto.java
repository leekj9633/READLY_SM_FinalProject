package com.tricode.READLY.domain.book.dto;

public class BookDto {

    // 홈화면 인기 도서 응답용
    public record PopularResponse(
            String name,
            String coverImageUrl
    ) {}

    // 마이페이지 읽은 책 목록 응답용
    public record MyListResponse(
            Long bookId,
            String name,
            String coverImageUrl
    ) {}
}
