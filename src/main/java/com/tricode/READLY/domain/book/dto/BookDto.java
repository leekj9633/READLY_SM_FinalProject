package com.tricode.READLY.domain.book.dto;

import jakarta.validation.constraints.NotBlank;

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

    // 알라딘 ISBN 조회 결과 응답용
    public record IsbnResponse(
            Long bookId,
            String isbn13,
            String name,
            String writer,
            String coverImageUrl,
            Integer pageCount,
            Double width,
            Double height
    ) {
        public static IsbnResponse from(com.tricode.READLY.domain.book.entity.Book book) {
            return new IsbnResponse(
                    book.getId(),
                    book.getIsbn13(),
                    book.getName(),
                    book.getWriter(),
                    book.getCoverImageUrl(),
                    book.getPageCount(),
                    book.getWidth(),
                    book.getHeight()
            );
        }
    }

    // 도서 검색(알라딘 ItemSearch) 결과 한 건.
    // 프론트는 목록에서 한 권을 고른 뒤 이 isbn13을 그대로 등록 요청에 실어 보낸다.
    public record SearchResponse(
            String isbn13,
            String name,
            String writer,
            String coverImageUrl
    ) {
        public static SearchResponse from(AladinDto.Item item) {
            return new SearchResponse(
                    item.isbn13(),
                    item.title(),
                    item.author(),
                    item.cover()
            );
        }
    }

    // 책 등록 요청용.
    // 수기 입력은 지원하지 않는다. 제목/저자/표지 등 서지 정보는 전부 알라딘에서 다시 가져오므로
    // 클라이언트가 보낼 값은 어떤 책인지 가리키는 isbn13 하나뿐이다.
    // (예전에는 서지 정보를 그대로 받아 저장했는데, 그 경로로 들어온 책은 isbn13이 NULL이라
    //  같은 책이 여러 행으로 쌓였다. db/2026-08-17-backfill-book-isbn13.sql 참고)
    public record CreateRequest(
            @NotBlank(message = "isbn13은 필수입니다.")
            String isbn13
    ) {}
}
