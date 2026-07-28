package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.dto.BookclubDto; // DTO import 추가
import com.tricode.READLY.domain.book.entity.BookClub;
import com.tricode.READLY.domain.book.repository.BookclubRepository; // 누락된 import 추가 가정
import com.tricode.READLY.domain.book.repository.MemberBookclubRepository;
import com.tricode.READLY.domain.chat.entity.ChatMessage;
import com.tricode.READLY.domain.chat.service.ChatProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookclubService {

    private final BookclubRepository bookClubRepository;
    private final MemberBookclubRepository memberBookclubRepository;
    private final ChatProducer chatProducer;

    /**
     * 기능 2: 홈화면에서 독서모임의 상세 정보 보기
     */
    public List<BookclubDto.HomeListResponse> getHomeBookClubs() {
        List<BookClub> clubs = bookClubRepository.findAll();

        return clubs.stream().map(club -> {
            int currentMemberCount = memberBookclubRepository.countByBookClubId(club.getId());

            // HomeBookClubListDto 대신 BookclubDto.HomeListResponse 사용
            return new BookclubDto.HomeListResponse(
                    club.getName(),
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
     * 기능 5: 독서모임 만들기
     */
    @Transactional
    public Long createBookClub(BookclubDto.CreateRequest request) { // CreateBookClubRequest 대신 BookclubDto.CreateRequest 사용
        BookClub bookClub = BookClub.builder()
                .name(request.name()) // record는 getter가 필드명() 형태입니다.
                .creationDate(request.date())
                .creationTime(request.time())
                .memberCapacity(request.maxCapacity())
                .type(request.type())
                .status(BookClub.ClubStatus.PENDING)
                .build();

        bookClubRepository.save(bookClub);
        return bookClub.getId();
    }

    /**
     * 기능 6: 독서모임에서 AI 및 다른 사용자들과 채팅하기
     */
    public void sendMessageToBookClub(Long clubId, Long memberId, String content) {
        // ChatMessage 구조(빌더 또는 생성자)에 맞게 객체를 생성합니다.
        // (주의: ChatMessage 엔티티에 선언된 실제 필드명에 맞게 .clubId(), .memberId() 부분을 맞춰주셔야 합니다.)
        ChatMessage message = ChatMessage.builder()
                .clubId(clubId)       // 실제 필드명으로 수정 필요 (예: bookClubId 등)
                .memberId(memberId)
                .content(content)
                .build();

        // 3개의 매개변수 대신 생성한 객체 1개를 넘겨줍니다.
        chatProducer.sendMessage(message);

        }
}