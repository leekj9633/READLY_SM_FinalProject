package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.dto.BookClubDto;
import com.tricode.READLY.domain.book.entity.Book;
import com.tricode.READLY.domain.book.entity.BookClub;
import com.tricode.READLY.domain.book.entity.MemberBookClub;
import com.tricode.READLY.domain.book.repository.BookClubRepository;
import com.tricode.READLY.domain.book.repository.BookRepository;
import com.tricode.READLY.domain.book.repository.MemberBookClubRepository;
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
public class BookClubService {

    private final BookClubRepository bookClubRepository;
    private final MemberBookClubRepository memberBookclubRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    /**
     * 기능 2: 홈화면에서 독서모임의 상세 정보 보기
     */
    public List<BookClubDto.HomeListResponse> getHomeBookClubs() {
        List<BookClub> clubs = bookClubRepository.findAllWithBook();

        return clubs.stream().map(club -> {
            int currentMemberCount = memberBookclubRepository.countByBookClubId(club.getId());
            Book book = club.getBook(); // 아직 책이 연결되지 않은 기존 모임은 null일 수 있다

            return new BookClubDto.HomeListResponse(
                    club.getId(),
                    club.getName(),
                    book != null ? book.getId() : null,
                    book != null ? book.getName() : null,
                    book != null ? book.getCoverImageUrl() : null,
                    club.getCreationDate(),
                    club.getCreationTime(),
                    currentMemberCount,
                    club.getMemberCapacity(),
                    club.getStatus(),
                    club.getType()
            );
        }).collect(Collectors.toList());
    }

    /**
     * 기능 5: 독서모임 만들기 (만든 사람은 자동으로 가입된다)
     */
    @Transactional
    public Long createBookClub(Long memberId, BookClubDto.CreateRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 책입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        BookClub bookClub = BookClub.builder()
                .name(request.name())
                .book(book)
                .creationDate(request.date())
                .creationTime(request.time())
                .memberCapacity(request.maxCapacity())
                .type(request.type())
                .status(BookClub.ClubStatus.PENDING)
                .build();

        bookClubRepository.save(bookClub);

        memberBookclubRepository.save(MemberBookClub.builder()
                .member(member)
                .bookClub(bookClub)
                .build());

        return bookClub.getId();
    }

    /**
     * 독서모임 가입하기
     */
    @Transactional
    public void joinBookClub(Long clubId, Long memberId) {
        BookClub bookClub = bookClubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 독서모임입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (memberBookclubRepository.existsByMemberIdAndBookClubId(memberId, clubId)) {
            throw new IllegalStateException("이미 가입한 독서모임입니다.");
        }
        if (bookClub.getStatus() == BookClub.ClubStatus.COMPLETED) {
            throw new IllegalStateException("이미 종료된 독서모임입니다.");
        }
        if (memberBookclubRepository.countByBookClubId(clubId) >= bookClub.getMemberCapacity()) {
            throw new IllegalStateException("정원이 가득 찬 독서모임입니다.");
        }

        memberBookclubRepository.save(MemberBookClub.builder()
                .member(member)
                .bookClub(bookClub)
                .build());
    }

    /**
     * 내가 가입한 독서모임 목록
     */
    public List<BookClubDto.HomeListResponse> getMyBookClubs(Long memberId) {
        return memberBookclubRepository.findAllByMemberIdWithBookClub(memberId).stream()
                .map(MemberBookClub::getBookClub)
                .map(club -> {
                    Book book = club.getBook();
                    return new BookClubDto.HomeListResponse(
                            club.getId(),
                            club.getName(),
                            book != null ? book.getId() : null,
                            book != null ? book.getName() : null,
                            book != null ? book.getCoverImageUrl() : null,
                            club.getCreationDate(),
                            club.getCreationTime(),
                            memberBookclubRepository.countByBookClubId(club.getId()),
                            club.getMemberCapacity(),
                            club.getStatus(),
                            club.getType()
                    );
                })
                .collect(Collectors.toList());
    }
}
