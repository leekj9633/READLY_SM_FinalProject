package com.tricode.READLY.domain.book.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricode.READLY.domain.book.dto.AladinDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * 알라딘 API 호출/파싱 검증.
 * 실제 알라딘 서버 대신 MockRestServiceServer로 응답을 흉내 낸다.
 */
class AladinBookClientTest {

    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
    private static final String DEMIAN_ISBN = "9788937460449";

    // 알라딘 서버에서 실제로 받은 응답(데미안, 민음사)을 그대로 옮긴 샘플
    private static final String DEMIAN_JSON = """
            {
              "version": "20131101",
              "title": "알라딘 상품정보 - 데미안",
              "item": [
                {
                  "title": "데미안",
                  "author": "헤르만 헤세 (지은이), 전영애 (옮긴이)",
                  "cover": "https://image.aladin.co.kr/product/26/0/coversum/s452139198_1.jpg",
                  "isbn13": "9788937460449",
                  "publisher": "민음사",
                  "priceStandard": 12000,
                  "subInfo": {
                    "originalTitle": "Demian: Die Geschichte von Emil Sinclairs Jugend (1919년)",
                    "itemPage": 248,
                    "packing": {
                      "styleDesc": "반양장본",
                      "weight": 327,
                      "sizeDepth": 14,
                      "sizeHeight": 225,
                      "sizeWidth": 132
                    }
                  }
                }
              ]
            }
            """;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private AladinBookClient aladinBookClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        aladinBookClient = new AladinBookClient(restTemplate, new ObjectMapper());
        ReflectionTestUtils.setField(aladinBookClient, "baseUrl", BASE_URL);
        ReflectionTestUtils.setField(aladinBookClient, "ttbKey", "YOUR_TTB_KEY");
    }

    @Test
    @DisplayName("ISBN13으로 조회하면 제목/저자/커버/페이지수/가로/세로를 파싱한다")
    void lookUpByIsbn13_parsesAllFields() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(BASE_URL)))
                .andExpect(queryParam("itemIdType", "ISBN13"))
                .andExpect(queryParam("ItemId", DEMIAN_ISBN))
                .andExpect(queryParam("ttbkey", "YOUR_TTB_KEY"))
                .andExpect(queryParam("OptResult", "packing,itemPage"))
                .andRespond(withSuccess(DEMIAN_JSON, MediaType.APPLICATION_JSON));

        AladinDto.Item item = aladinBookClient.lookUpByIsbn13(DEMIAN_ISBN);

        assertThat(item.title()).isEqualTo("데미안");
        assertThat(item.author()).isEqualTo("헤르만 헤세 (지은이), 전영애 (옮긴이)");
        assertThat(item.cover()).isEqualTo("https://image.aladin.co.kr/product/26/0/coversum/s452139198_1.jpg");
        assertThat(item.subInfo().itemPage()).isEqualTo(248);
        assertThat(item.subInfo().packing().sizeWidth()).isEqualTo(132);   // 가로(mm)
        assertThat(item.subInfo().packing().sizeHeight()).isEqualTo(225);  // 세로(mm)

        mockServer.verify();
    }

    @Test
    @DisplayName("TTB Key가 잘못되면 알라딘 오류 메시지를 그대로 예외로 던진다")
    void lookUpByIsbn13_throwsOnErrorResponse() {
        String errorJson = """
                {"errorCode":100,"errorMessage":"잘못된 TTB key 입니다."}
                """;

        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(BASE_URL)))
                .andRespond(withSuccess(errorJson, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> aladinBookClient.lookUpByIsbn13(DEMIAN_ISBN))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("잘못된 TTB key");
    }

    @Test
    @DisplayName("검색 결과가 없으면 예외를 던진다")
    void lookUpByIsbn13_throwsWhenEmpty() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.startsWith(BASE_URL)))
                .andRespond(withSuccess("{\"item\":[]}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> aladinBookClient.lookUpByIsbn13(DEMIAN_ISBN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없는 ISBN");
    }
}
