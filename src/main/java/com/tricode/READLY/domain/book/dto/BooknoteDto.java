package com.tricode.READLY.domain.book.dto;

public class BooknoteDto {

    // 독서록 작성 요청용 (직접 입력 or 텍스트 인식)
    public record CreateRequest(
            String phrase,
            String feeling
    ) {}

    // AI가 작성한 독서록 수정 요청용
    public record UpdateAiRequest(
            String newAiContent
    ) {}
}
