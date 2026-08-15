package com.tricode.READLY.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 알라딘 ItemLookUp API 응답 파싱용 DTO.
 * 응답 필드가 매우 많으므로 필요한 것만 선언하고 나머지는 무시한다.
 */
public class AladinDto {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LookUpResponse(
            List<Item> item,
            Integer errorCode,      // 키가 잘못되었거나 조회 실패 시에만 내려온다
            String errorMessage
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String title,           // 책 제목
            String author,          // 저자
            String cover,           // 커버 이미지 URL
            String isbn13,
            SubInfo subInfo         // OptResult=packing,itemPage 를 넘겨야 채워진다
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SubInfo(
            Integer itemPage,       // 페이지 수
            Packing packing         // 판형 정보
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Packing(
            Integer sizeWidth,      // 가로 (mm)
            Integer sizeHeight,     // 세로 (mm)
            Integer sizeDepth,      // 두께 (mm)
            Integer weight,
            String styleDesc
    ) {}
}
