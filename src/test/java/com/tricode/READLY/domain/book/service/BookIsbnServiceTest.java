package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.client.AladinBookClient;
import com.tricode.READLY.domain.book.dto.AladinDto;
import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * ISBN 기준 조회/저장 로직 검증.
 * 알라딘 서버는 목으로 대체하고, DB 저장은 실제 JPA로 수행한다.
 */
@DataJpaTest
@Import(BookService.class)
class BookIsbnServiceTest {

    private static final String DEMIAN_ISBN = "9788937460449";

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AladinBookClient aladinBookClient;

    private AladinDto.Item demianItem() {
        return new AladinDto.Item(
                "데미안",
                "헤르만 헤세 (지은이), 전영애 (옮긴이)",
                "https://image.aladin.co.kr/product/26/0/coversum/s452139198_1.jpg",
                DEMIAN_ISBN,
                new AladinDto.SubInfo(248,
                        new AladinDto.Packing(132, 225, 14, 327, "반양장본"))
        );
    }

    @Test
    @DisplayName("DB에 없는 ISBN이면 알라딘에서 가져와 Book 테이블에 저장한다")
    void savesNewBookFromAladin() {
        given(aladinBookClient.lookUpByIsbn13(DEMIAN_ISBN)).willReturn(demianItem());

        Book saved = bookService.getOrCreateBookByIsbn13(DEMIAN_ISBN);
        entityManager.flush();
        entityManager.clear();

        Book found = bookRepository.findByIsbn13(DEMIAN_ISBN).orElseThrow();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("데미안");
        assertThat(found.getWriter()).isEqualTo("헤르만 헤세 (지은이), 전영애 (옮긴이)");
        assertThat(found.getCoverImageUrl()).contains("aladin.co.kr");
        assertThat(found.getPageCount()).isEqualTo(248);
        assertThat(found.getWidth()).isEqualTo(132.0);   // 가로(mm)
        assertThat(found.getHeight()).isEqualTo(225.0);  // 세로(mm)
    }

    @Test
    @DisplayName("이미 저장된 ISBN이면 알라딘 API를 호출하지 않고 기존 엔티티를 반환한다")
    void reusesExistingBookWithoutCallingAladin() {
        Book existing = bookRepository.save(Book.builder()
                .isbn13(DEMIAN_ISBN)
                .name("데미안")
                .writer("헤르만 헤세")
                .build());
        entityManager.flush();
        entityManager.clear();

        Book result = bookService.getOrCreateBookByIsbn13(DEMIAN_ISBN);

        assertThat(result.getId()).isEqualTo(existing.getId());
        verify(aladinBookClient, never()).lookUpByIsbn13(anyString());
        assertThat(bookRepository.count()).isEqualTo(1);
    }
}
