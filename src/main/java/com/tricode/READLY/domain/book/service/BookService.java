package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.dto.BookDto;
import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.MemberBook;
import com.tricode.READLY.domain.book.repository.BookRepository;
import com.tricode.READLY.domain.book.repository.MemberBookRepository;
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
}
