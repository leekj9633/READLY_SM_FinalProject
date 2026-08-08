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

    // 책 등록 요청용 (외부 도서 API에서 가져온 정보를 그대로 넘겨받는다고 가정)
    public record CreateRequest(
            String name,
            String writer,
            String coverImageUrl,
            Integer pageCount,
            Double width,
            Double height
    ) {}
}
