package com.tricode.READLY.domain.book.client;

import com.tricode.READLY.domain.book.dto.AladinDto;
import com.tricode.READLY.global.config.RestTemplateConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 알라딘 서버를 호출하는 테스트.
 * 네트워크와 유효한 TTB Key가 필요하므로 평소에는 건너뛰고,
 * -Daladin.live=true 를 줄 때만 실행한다.
 *
 * 실행: .\gradlew.bat test --tests "*AladinBookClientLiveTest" -Daladin.live=true
 */
@EnabledIfSystemProperty(named = "aladin.live", matches = "true")
@SpringBootTest(classes = {RestTemplateConfig.class, AladinBookClient.class, JacksonAutoConfiguration.class})
class AladinBookClientLiveTest {

    private static final String DEMIAN_ISBN = "9788937460449"; // 데미안 (민음사)

    @Autowired
    private AladinBookClient aladinBookClient;

    @Test
    @DisplayName("실제 알라딘 API에서 데미안 정보를 가져와 6개 항목을 모두 채운다")
    void lookUpDemianFromRealApi() {
        AladinDto.Item item = aladinBookClient.lookUpByIsbn13(DEMIAN_ISBN);

        System.out.println("[알라딘 응답] title=" + item.title()
                + ", author=" + item.author()
                + ", cover=" + item.cover()
                + ", itemPage=" + item.subInfo().itemPage()
                + ", sizeWidth=" + item.subInfo().packing().sizeWidth()
                + ", sizeHeight=" + item.subInfo().packing().sizeHeight());

        assertThat(item.title()).contains("데미안");
        assertThat(item.author()).contains("헤르만 헤세");
        assertThat(item.cover()).startsWith("https://image.aladin.co.kr");
        assertThat(item.isbn13()).isEqualTo(DEMIAN_ISBN);
        assertThat(item.subInfo().itemPage()).isPositive();
        assertThat(item.subInfo().packing().sizeWidth()).isPositive();
        assertThat(item.subInfo().packing().sizeHeight()).isPositive();
    }
}
