package com.tricode.READLY.domain.book.dto;

import java.util.List;

public class BookNoteDto {

    // 독서록 작성 요청용 (직접 입력 or 텍스트 인식)
    public record CreateRequest(
            String phrase,
            String feeling
    ) {}

    // AI가 작성한 독서록 수정 요청용
    public record UpdateAiRequest(
            String newAiContent
    ) {}

    // AI 서버에 성향 태그 분석 요청 (POST /api/preference/analyze)
    public record TagAnalyzeRequest(
            String review
    ) {}

    // AI 서버가 반환하는 성향 태그 목록
    public record TagAnalyzeResponse(
            List<String> tags
    ) {}
}
