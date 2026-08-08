package com.tricode.READLY.domain.book.service;

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
     * 책 등록하기 (외부 도서 API에서 검색한 책을 우리 DB에 저장)
     */
    @Transactional
    public Long registerBook(BookDto.CreateRequest request) {
        Book book = Book.builder()
                .name(request.name())
                .writer(request.writer())
                .coverImageUrl(request.coverImageUrl())
                .pageCount(request.pageCount())
                .width(request.width())
                .height(request.height())
                .build();

        bookRepository.save(book);
        return book.getId();
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
