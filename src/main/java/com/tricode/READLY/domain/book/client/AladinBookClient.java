package com.tricode.READLY.domain.book.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricode.READLY.domain.book.dto.AladinDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * 알라딘 상품 조회(ItemLookUp) / 상품 검색(ItemSearch) API 호출 담당.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AladinBookClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aladin.base-url}")
    private String baseUrl;

    @Value("${aladin.search-url}")
    private String searchUrl;

    @Value("${aladin.ttb-key}")
    private String ttbKey;

    /**
     * ISBN13으로 책 한 권을 조회한다.
     * OptResult=packing,itemPage 를 넣어야 subInfo(페이지 수/판형)가 응답에 포함된다.
     */
    public AladinDto.Item lookUpByIsbn13(String isbn13) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ttbkey", ttbKey)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("ItemId", isbn13)
                .queryParam("output", "js")          // JSON으로 응답받기
                .queryParam("Version", "20131101")
                .queryParam("OptResult", "packing,itemPage")
                .build()
                .encode()
                .toUri();

        // 알라딘은 Content-Type을 text/html로 내려줄 때가 있어 String으로 받아서 직접 파싱한다
        String body = restTemplate.getForObject(uri, String.class);
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("알라딘 API 응답이 비어 있습니다. (isbn13: " + isbn13 + ")");
        }

        AladinDto.LookUpResponse response = parse(body, "isbn13: " + isbn13);

        if (response.errorCode() != null) {
            log.error("알라딘 API 오류 (isbn13: {}, code: {}): {}", isbn13, response.errorCode(), response.errorMessage());
            throw new IllegalStateException("알라딘 API 오류: " + response.errorMessage());
        }
        if (response.item() == null || response.item().isEmpty()) {
            throw new IllegalArgumentException("알라딘에서 찾을 수 없는 ISBN입니다: " + isbn13);
        }

        return response.item().get(0);
    }

    /**
     * 제목으로 책을 검색한다. 사용자가 목록에서 한 권을 고르면 그 isbn13으로 등록이 이어진다.
     * 검색 결과가 없는 것은 오류가 아니므로 빈 목록을 돌려준다.
     * 상세 정보(페이지 수/판형)는 등록 시점의 ItemLookUp에서 채우므로 여기서는 OptResult를 넘기지 않는다.
     */
    public List<AladinDto.Item> searchByTitle(String keyword, int maxResults) {
        URI uri = UriComponentsBuilder.fromUriString(searchUrl)
                .queryParam("ttbkey", ttbKey)
                .queryParam("Query", keyword)
                .queryParam("QueryType", "Title")
                .queryParam("SearchTarget", "Book")
                .queryParam("MaxResults", maxResults)
                .queryParam("start", 1)
                .queryParam("output", "js")
                .queryParam("Version", "20131101")
                .build()
                .encode()          // 한글 검색어를 UTF-8로 퍼센트 인코딩한다
                .toUri();

        String body = restTemplate.getForObject(uri, String.class);
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("알라딘 API 응답이 비어 있습니다. (keyword: " + keyword + ")");
        }

        AladinDto.LookUpResponse response = parse(body, "keyword: " + keyword);

        if (response.errorCode() != null) {
            log.error("알라딘 검색 API 오류 (keyword: {}, code: {}): {}",
                    keyword, response.errorCode(), response.errorMessage());
            throw new IllegalStateException("알라딘 API 오류: " + response.errorMessage());
        }

        return response.item() != null ? response.item() : List.of();
    }

    private AladinDto.LookUpResponse parse(String body, String context) {
        // 응답 끝에 세미콜론이 붙어 오는 경우가 있어 제거 후 파싱
        String json = body.trim();
        if (json.endsWith(";")) {
            json = json.substring(0, json.length() - 1);
        }

        try {
            return objectMapper.readValue(json, AladinDto.LookUpResponse.class);
        } catch (Exception e) {
            log.error("알라딘 API 응답 파싱 실패 ({}): {}", context, body);
            throw new IllegalStateException("알라딘 API 응답을 해석할 수 없습니다.", e);
        }
    }
}
