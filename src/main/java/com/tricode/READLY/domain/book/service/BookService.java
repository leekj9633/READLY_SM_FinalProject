package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.client.AladinBookClient;
import com.tricode.READLY.domain.book.dto.AladinDto;
import com.tricode.READLY.domain.book.dto.BookDto;
import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.MemberBook;
import com.tricode.READLY.domain.book.repository.BookRepository;
import com.tricode.READLY.domain.book.repository.MemberBookRepository;
import com.tricode.READLY.domain.member.entity.Member;
import com.tricode.READLY.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final MemberBookRepository memberBookRepository;
    private final MemberRepository memberRepository;
    private final AladinBookClient aladinBookClient;

    private static final int SEARCH_MAX_RESULTS = 20; // 알라딘 검색 결과로 한 번에 받아올 최대 권수

    /**
     * 홈화면에서 가장 인기가 많은 책의 제목과 커버 이미지 보여주기
     *      (MemberBook에 가장 많이 매핑된 책을 인기 책으로 가정)
     */
    public BookDto.PopularResponse getMostPopularBook() {
        // Repository에서 Query를 통해 읽은 사람이 가장 많은 책을 1권 가져오기
        Book popularBook = bookRepository.findTopByOrderByMemberBooksDesc()
                .orElseThrow(() -> new IllegalArgumentException("등록된 책이 없습니다."));

        return new BookDto.PopularResponse(
                popularBook.getName(),
                popularBook.getCoverImageUrl()
        );
    }

    /**
     * 마이페이지에서 내가 읽은 책 목록 확인하기
     */
    public List<BookDto.MyListResponse> getMyReadBooks(Long memberId) {
        // Fetch Join을 사용해 MemberBook과 Book을 한 번에 조회
        List<MemberBook> memberBooks = memberBookRepository.findAllByMemberIdWithBook(memberId);

        return memberBooks.stream()
                .map(mb -> new BookDto.MyListResponse(
                        mb.getBook().getId(),
                        mb.getBook().getName(),
                        mb.getBook().getCoverImageUrl()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 제목으로 책 검색하기 (알라딘 ItemSearch).
     * 우리 DB에 저장하지는 않는다. 사용자가 결과 중 한 권을 고르면 그 isbn13으로 등록이 이어진다.
     */
    public List<BookDto.SearchResponse> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해 주세요.");
        }

        return aladinBookClient.searchByTitle(keyword.trim(), SEARCH_MAX_RESULTS).stream()
                .map(BookDto.SearchResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 책 등록하기.
     * 수기 입력은 받지 않는다. 검색 결과에서 고른 isbn13만 받아,
     * 이미 있으면 그 행을 재사용하고 없으면 알라딘에서 상세를 받아 저장한다.
     */
    @Transactional
    public Long registerBook(BookDto.CreateRequest request) {
        return getOrCreateBookByIsbn13(request.isbn13()).getId();
    }

    /**
     * ISBN13으로 책 가져오기.
     * 이미 DB에 있으면 알라딘 API를 호출하지 않고 그대로 반환하고,
     * 없으면 알라딘에서 조회한 뒤 Book 테이블에 저장한다.
     */
    @Transactional
    public Book getOrCreateBookByIsbn13(String isbn13) {
        return bookRepository.findByIsbn13(isbn13)
                .orElseGet(() -> bookRepository.save(toBook(isbn13, aladinBookClient.lookUpByIsbn13(isbn13))));
    }

    // 알라딘 응답을 Book 엔티티로 변환 (subInfo는 OptResult 옵션이 빠지면 null일 수 있어 방어적으로 읽는다)
    private Book toBook(String isbn13, AladinDto.Item item) {
        AladinDto.SubInfo subInfo = item.subInfo();
        AladinDto.Packing packing = subInfo != null ? subInfo.packing() : null;

        return Book.builder()
                .isbn13(item.isbn13() != null ? item.isbn13() : isbn13)
                .name(item.title())
                .writer(item.author())
                .coverImageUrl(item.cover())
                .pageCount(subInfo != null ? subInfo.itemPage() : null)
                .width(packing != null && packing.sizeWidth() != null ? packing.sizeWidth().doubleValue() : null)
                .height(packing != null && packing.sizeHeight() != null ? packing.sizeHeight().doubleValue() : null)
                .build();
    }

    /**
     * 내가 읽은 책 목록에 책 추가하기
     */
    @Transactional
    public void addBookToMyList(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (memberBookRepository.existsByMemberIdAndBookId(memberId, bookId)) {
            throw new IllegalStateException("이미 목록에 담은 책입니다.");
        }

        memberBookRepository.save(MemberBook.builder()
                .member(member)
                .book(book)
                .build());
    }
}
