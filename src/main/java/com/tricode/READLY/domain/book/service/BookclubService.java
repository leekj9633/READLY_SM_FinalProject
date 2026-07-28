package com.tricode.READLY.domain.book.service;

import com.tricode.READLY.domain.book.entity.BookClub;
import com.tricode.READLY.domain.book.repository.MemberBookclubRepository;
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

    private final BookClubRepository bookClubRepository;
    private final MemberBookclubRepository memberBookclubRepository;

    // 채팅 발송 처리를 위한 Producer (구조 상 chat 패키지에 위치)
    private final ChatProducer chatProducer;

    /**
     * 기능 2: 홈화면에서 독서모임의 상세 정보(책 제목, 시간, 모인 인원 등) 보기
     */
    public List<HomeBookClubListDto> getHomeBookClubs() {
        List<BookClub> clubs = bookClubRepository.findAll();

        return clubs.stream().map(club -> {
            // 현재 모임에 가입된 중간 엔티티(MemberBookclub) 개수를 카운트하여 모인 인원 산출
            int currentMemberCount = memberBookclubRepository.countByBookClubId(club.getId());

            return new HomeBookClubListDto(
                    club.getName(), // 엔티티에 Book이 없으므로 임시로 Name 매핑
                    club.getCreationDate(),
                    club.getCreationTime(),
                    currentMemberCount,
                    club.getMemberCapacity(),
                    club.getStatus(), // 모집 중 여부 (PENDING, IN_PROGRESS 등)
                    club.getType()    // 열정도 타입 (PASSIONATE 등)
            );
        }).collect(Collectors.toList());
    }

    /**
     * 기능 5: 독서모임 만들기
     */
    @Transactional
    public Long createBookClub(CreateBookClubRequest request) {
        BookClub bookClub = BookClub.builder()
                .name(request.getName()) // 독서모임 이름
                .creationDate(request.getDate())
                .creationTime(request.getTime())
                .memberCapacity(request.getMaxCapacity())
                .type(request.getType())
                .status(BookClub.ClubStatus.PENDING) // 초기 상태를 모집중(PENDING)으로 설정
                .build();

        bookClubRepository.save(bookClub);
        return bookClub.getId();
    }

    /**
     * 기능 6: 독서모임에서 AI 및 다른 사용자들과 채팅하기 (Chat 도메인 연계)
     * 실제 메시지 저장은 WebSocket Controller를 통해 ChatProducer가 받아서 처리하게 됩니다.
     */
    public void sendMessageToBookClub(Long clubId, Long memberId, String content) {
        // 이미 생성하신 ChatProducer를 활용하여 Kafka 토픽으로 메시지 발행
        chatProducer.sendMessage(clubId, memberId, content);
    }
}
